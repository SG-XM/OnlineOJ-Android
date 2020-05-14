/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twt.zq.commons.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static android.app.ActivityManager.ProcessErrorStateInfo.NO_ERROR;

/**
 * Handler capable of restarting parts of the application in order for changes to become apparent to
 * the user:
 * <p>
 * <ul>
 * <li>Apply a tiny change immediately - possible if we can detect that the change is only used in
 * a limited context (such as in a layout) and we can directly poke the view hierarchy and
 * schedule a paint.
 * <li>Apply a change to the current activity. We can restart just the activity while the app
 * continues running.
 * <li>Restart the app with state persistence (simulates what happens when a user puts an app in
 * the background, then it gets killed by the memory monitor, and then restored when the user
 * brings it back
 * <li>Restart the app completely.
 * </ul>
 */
public class Restarter {
    public static String LOG_TAG = "Restarter";

    /**
     * Restart an activity. Should preserve as much state as possible.
     */
    public static void restartActivityOnUiThread(@NonNull final Activity activity) {
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                            Log.v(LOG_TAG, "Resources updated: notify activities");
                        }
                        updateActivity(activity);
                    }
                });
    }

    private static void restartActivity(@NonNull Activity activity) {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "About to restart " + activity.getClass().getSimpleName());
        }

        // You can't restart activities that have parents: find the top-most activity
        while (activity.getParent() != null) {
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(
                        LOG_TAG,
                        activity.getClass().getSimpleName()
                                + " is not a top level activity; restarting "
                                + activity.getParent().getClass().getSimpleName()
                                + " instead");
            }
            activity = activity.getParent();
        }

        // Directly supported by the framework!
        activity.recreate();
    }

    /**
     * Attempt to restart the app. Ideally this should also try to preserve as much state as
     * possible:
     * <ul>
     * <li>The current activity</li>
     * <li>If possible, state in the current activity, and</li>
     * <li>The activity stack</li>
     * </ul>
     * <p>
     * This may require some framework support. Apparently it may already be possible
     * (Dianne says to put the app in the background, kill it then restart it; need to
     * figure out how to do this.)
     */
    public static void restartApp(@Nullable Context appContext,
                                  @NonNull Collection<Activity> knownActivities,
                                  boolean toast) {
        if (!knownActivities.isEmpty()) {
            // Can't live patch resources; instead, try to restart the current activity
            Activity foreground = getForegroundActivity(appContext);

            if (foreground != null) {
                // http://stackoverflow.com/questions/6609414/howto-programatically-restart-android-app
                //noinspection UnnecessaryLocalVariable
                if (toast) {
                    showToast(foreground, "Restarting app to apply incompatible changes");
                }
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "RESTARTING APP");
                }
                @SuppressWarnings("UnnecessaryLocalVariable") // fore code clarify
                        Context context = foreground;
                Intent intent = new Intent(context, foreground.getClass());
                int intentId = 0;
                PendingIntent pendingIntent = PendingIntent.getActivity(context, intentId,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(
                            LOG_TAG,
                            "Scheduling activity "
                                    + foreground
                                    + " to start after exiting process");
                }
            } else {
                showToast(knownActivities.iterator().next(), "Unable to restart app");
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(
                            LOG_TAG,
                            "Couldn't find any foreground activities to restart "
                                    + "for resource refresh");
                }
            }
            System.exit(0);
        }
    }

    static void showToast(@NonNull final Activity activity, @NonNull final String text) {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "About to show toast for activity " + activity + ": " + text);
        }
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Context context = activity.getApplicationContext();
                            if (context instanceof ContextWrapper) {
                                Context base = ((ContextWrapper) context).getBaseContext();
                                if (base == null) {
                                    if (Log.isLoggable(LOG_TAG, Log.WARN)) {
                                        Log.w(LOG_TAG, "Couldn't show toast: no base context");
                                    }
                                    return;
                                }
                            }

                            // For longer messages, leave the message up longer
                            int duration = Toast.LENGTH_SHORT;
                            if (text.length() >= 60 || text.indexOf('\n') != -1) {
                                duration = Toast.LENGTH_LONG;
                            }

                            // Avoid crashing when not available, e.g.
                            //   java.lang.RuntimeException: Can't create handler inside thread that has
                            //        not called Looper.prepare()
                            Toast.makeText(activity, text, duration).show();
                        } catch (Throwable e) {
                            if (Log.isLoggable(LOG_TAG, Log.WARN)) {
                                Log.w(LOG_TAG, "Couldn't show toast", e);
                            }
                        }
                    }
                });
    }

    @Nullable
    public static Activity getForegroundActivity(@Nullable Context context) {
        List<Activity> list = getActivities(context, true);
        return list.isEmpty() ? null : list.get(0);
    }

    // http://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android
    @NonNull
    public static List<Activity> getActivities(@Nullable Context context, boolean foregroundOnly) {
        List<Activity> list = new ArrayList<>();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = getActivityThread(context, activityThreadClass);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            // check app hasn't crashed, if it has, return empty list of activities.
            if (hasAppCrashed(context, activityThreadClass, activityThread)) {
                return new ArrayList<Activity>();
            }

            Collection c;
            Object collection = activitiesField.get(activityThread);

            if (collection instanceof HashMap) {
                // Older platforms
                Map activities = (HashMap) collection;
                c = activities.values();
            } else if (collection instanceof ArrayMap) {
                ArrayMap activities = (ArrayMap) collection;
                c = activities.values();
            } else {
                return list;
            }

            for (Object activityClientRecord : c) {
                Class activityClientRecordClass = activityClientRecord.getClass();
                if (foregroundOnly) {
                    Field pausedField = activityClientRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (pausedField.getBoolean(activityClientRecord)) {
                        continue;
                    }
                }
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityClientRecord);
                if (activity != null) {
                    list.add(activity);
                }
            }
        } catch (Throwable e) {
            if (Log.isLoggable(LOG_TAG, Log.WARN)) {
                Log.w(LOG_TAG, "Error retrieving activities", e);
            }
        }
        return list;
    }

    /**
     * Checks if the application has crashed by comparing the package name against the list of
     * processes in error state.
     */
    private static boolean hasAppCrashed(
            @Nullable Context context,
            @NonNull Class activityThreadClass,
            @Nullable Object activityThread)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (context == null || activityThread == null) {
            return false;
        }

        String currentPackageName = getPackageName(activityThreadClass, activityThread);

        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.ProcessErrorStateInfo> processesInErrorState =
                manager.getProcessesInErrorState();
        if (processesInErrorState != null) { // returns null if no process in error state
            for (ActivityManager.ProcessErrorStateInfo info : processesInErrorState) {
                if (info.processName.equals(currentPackageName) && info.condition != NO_ERROR) {
                    if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                        Log.v(LOG_TAG, "App Thread has crashed, return empty activity list.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Use reflection to determine the package name from activity thread.
    private static String getPackageName(
            @NonNull Class activityThreadClass, @Nullable Object activityThread)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method currentPackageNameMethod =
                activityThreadClass.getDeclaredMethod("currentPackageName");
        return (String) currentPackageNameMethod.invoke(activityThread);
    }

    private static void updateActivity(@NonNull Activity activity) {
        // This method can be called for activities that are not in the foreground, as long
        // as some of its resources have been updated. Therefore we'll need to make sure
        // that this activity is in the foreground, and if not do nothing. Ways to do
        // that are outlined here:
        // http://stackoverflow.com/questions/3667022/checking-if-an-android-application-is-running-in-the-background/5862048#5862048

        // Try to force re-layout; there are many approaches; see
        // http://stackoverflow.com/questions/5991968/how-to-force-an-entire-layout-view-refresh

        // This doesn't seem to update themes properly -- may need to do recreate() instead!
        //getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

        // This is a bit of a sledgehammer. We should consider having an incremental updater,
        // similar to IntelliJ's Look &amp; Feel updater which iterates to the view hierarchy
        // and tries to incrementally refresh the LAF delegates and force a repaint.
        // On the other hand, we may never be able to succeed with that, since there could be
        // UI elements on the screen cached from callbacks. I should probably *not* attempt
        // to try to poke the user's data models; recreating the current layout should be
        // enough (e.g. if a layout references @string/foo, we'll recreate those widgets
        //    if (mLastContentView != -1) {
        //        setContentView(mLastContentView);
        //    } else {
        //        recreate();
        //    }
        // -- nope, even that's iffy. I had code which *after* calling setContentView would
        // do some findViewById calls etc to reinitialize views.
        //
        // So what I should really try to do is have some knowledge about what changed,
        // and see if I can figure out that the change is minor (e.g. doesn't affect themes
        // or layout parameters etc), and if so, just try to poke the view hierarchy directly,
        // and if not, just recreate

        //    if (changeManager.isSimpleDelta()) {
        //        changeManager.applyDirectly(this);
        //    } else {

        // Note: This doesn't handle manifest changes like changing the application title

        restartActivity(activity);
    }

    @Nullable
    public static Object getActivityThread(@Nullable Context context,
                                           @Nullable Class<?> activityThread) {
        try {
            if (activityThread == null) {
                activityThread = Class.forName("android.app.ActivityThread");
            }
            Method m = activityThread.getMethod("currentActivityThread");
            m.setAccessible(true);
            Object currentActivityThread = m.invoke(null);
            if (currentActivityThread == null && context != null) {
                // In older versions of Android (prior to frameworks/base 66a017b63461a22842)
                // the currentActivityThread was built on thread locals, so we'll need to try
                // even harder
                Field mLoadedApk = context.getClass().getField("mLoadedApk");
                mLoadedApk.setAccessible(true);
                Object apk = mLoadedApk.get(context);
                Field mActivityThreadField = apk.getClass().getDeclaredField("mActivityThread");
                mActivityThreadField.setAccessible(true);
                currentActivityThread = mActivityThreadField.get(apk);
            }
            return currentActivityThread;
        } catch (Throwable ignore) {
            return null;
        }
    }
}

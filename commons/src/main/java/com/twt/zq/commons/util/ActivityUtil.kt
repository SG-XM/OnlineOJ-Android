package com.twt.zq.commons.util

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.twt.zq.commons.R
import com.twt.zq.commons.common.CommonPreferences
import com.twt.zq.commons.extentions.ExGlideEngine
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * Created by SGXM on 2019/2/27.
 */
class ActivityUtil {
    companion object {


        public fun isPad(context: Context): Boolean {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.getDefaultDisplay();
            // 屏幕宽度
            val screenWidth = display.getWidth()
            // 屏幕高度
            val screenHeight = display.getHeight()
            val dm = DisplayMetrics()
            display.getMetrics(dm)
            val x = Math.pow((dm.widthPixels / dm.xdpi).toDouble(), 2.0)
            val y = Math.pow((dm.heightPixels / dm.ydpi).toDouble(), 2.0)
            // 屏幕尺寸
            val screenInches = Math.sqrt(x + y)
            // 大于6尺寸则为Pad
            return screenInches >= 6.0 && (context.getResources().getConfiguration().screenLayout.and(Configuration.SCREENLAYOUT_SIZE_MASK)
                    ) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        fun showSoftInputFromWindow(editText: EditText, window: Window) {
            editText.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        /**
         * 获取手机型号
         */
        private fun getMobileType() = Build.MANUFACTURER

//        fun ignoreBatteryOptimization(ac: Activity) {
//            val pwManager = ac.getSystemService(Activity.POWER_SERVICE) as PowerManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!pwManager.isIgnoringBatteryOptimizations(ac.packageName) && CommonPreferences.hasIgnoredBattery.toInt() in 1..9) {
//                    CommonPreferences.hasIgnoredBattery = (CommonPreferences.hasIgnoredBattery.toInt() + 1).toString()
//                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                    intent.data = Uri.parse("package:" + ac.packageName)
//                    //有些手机阉割了导致该 intent 无响应对象
//                    intent.resolveActivity(ac.packageManager)?.let {
//                        ac.startActivity(intent)
//                    }
//                }
//            }
//        }

        fun getWakeLock(context: Context) {
            /**
             * If you must use partial wake locks, follow these recommendations:
            1.Make sure some portion of your app remains in the foreground. For example,
            if you need to run a service, start a foreground service instead.
            This visually indicates to the user that your app is still running.
            2.Make sure the logic for acquiring and releasing wake locks is as simple as possible.
            When your wake lock logic is tied to complex state machines, timeouts, executor pools, and/or callback events,
            any subtle bug in that logic can cause the wake lock to be held longer than expected.
            These bugs are difficult to diagnose and debug.
            -- Google Document
             */
            try {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "acu:woggle")
                wakeLock.acquire()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun setStatusBarDarkTrans(ac: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ac.window.statusBarColor = ac.resources.getColor(R.color.darkGrey, null)
                } else ac.window.statusBarColor = ac.resources.getColor(R.color.darkGrey)
            }
        }

        fun setStatusBarPrim(ac: Activity) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    ac.window.statusBarColor = ac.resources.getColor(R.color.colorPrimary, null)
//                } else ac.window.statusBarColor = ac.resources.getColor(R.color.colorPrimary)
//            }
        }

        fun showDialog(
            context: Context,
            msg: String,
            title: String = "通知",
            posTitle: String = "确定",
            negTitle: String = "取消",
            posCallback: () -> Unit,
            negCallback: () -> Unit
        ) {
            AlertDialog.Builder(context).apply {
                setTitle(title)
                setMessage(msg)
                setPositiveButton(posTitle) { _, which ->
                    if (DialogInterface.BUTTON_POSITIVE == which) {
                        posCallback()
                    }
                }
                setNegativeButton(negTitle) { _, which ->
                    if (DialogInterface.BUTTON_NEGATIVE == which) {
                        negCallback()
                    }
                }
            }.show()
        }

        fun getWithAndHeight(context: Context): Pair<Int, Int> {
            val dis = context.windowManager.defaultDisplay
            val point = Point()
            dis.getSize(point)
            return Pair(point.x, point.y)
        }

        fun questAutoStartPermission(context: Context) {
            val intent = Intent()
            try {
                CommonPreferences.isAutoStarted = "1"
                Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType())
                intent.apply {
                    when (getMobileType()) {
                        "Xiaomi" -> component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                        "Letv" -> action = "com.letv.android.permissionautoboot"
                        "samsung" -> component =
                            ComponentName.unflattenFromString("com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity")
                        "HUAWEI" -> component =
                            ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity")
                        "vivo" -> component =
                            ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity")
                        "Meizu" -> component =
                            ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity")//跳转到后台管理页面
                        "OPPO" -> component =
                            ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity")
                        "ulong" -> component =
                            ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity")
                        else -> {
                            Log.e("HLQ_Struggle", "APPLICATION_DETAILS_SETTINGS")
                            action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    }
                    resolveActivity(context.packageManager)?.let {
                        context.startActivity(this)
                    } ?: throw NullPointerException("Failed in resolving AutoStartInfoActivity")
                }

            } catch (e: Exception) {//抛出异常就直接打开设置页面
                Log.e("HLQ_Struggle", e.localizedMessage)
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }
}

fun Activity.exCheckPermissions(permission: Array<String>, requestCode: Int, callback: () -> Unit) {
    permission.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            val a = arrayOf(it)
            ActivityCompat.requestPermissions(this, a, requestCode)
            return
        }
    }
    callback()
}

fun Activity.exCheckPermission(permission: String, requestCode: Int, callback: () -> Unit) {
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        val a = arrayOf(permission)
        ActivityCompat.requestPermissions(this, a, requestCode)
    } else {
        callback()
    }
}

fun Activity.selectPic(code: Int, num: Int = 1) {
    exCheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE, code) {
        Matisse.from(this)
            .choose(MimeType.ofImage(), false) // 选择 mime 的类型
            .countable(true)
            .maxSelectable(num) // 图片选择的最多数量
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.8f) // 缩略图的比例
            .imageEngine(ExGlideEngine()) // 使用的图片加载引擎
            .forResult(code)
    }
}


fun Activity.modifyText(hintStr: String = "", textView: TextView, callback: (String) -> Unit) {
    alert {
        customView {
            constraintLayout {
                lparams {
                    width = dip(300)
                    height = wrapContent
                    padding = dip(12)
                }
                val edt = editText {
                    hint = hintStr
                }.lparams {
                    topToTop = androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
                    width = matchParent
                    topMargin = dip(24)
                }
                positiveButton("确认") {
                    textView.text = edt.text
                    callback(edt.text.toString())
                }
                negativeButton("取消") {}
            }
        }
    }.show()
}

fun Activity.modifyText(hintStr: String = "", callback: (String) -> Unit) {
    alert {
        customView {
            constraintLayout {
                lparams {
                    width = dip(300)
                    height = wrapContent
                    padding = dip(12)
                }
                val edt = editText {
                    hint = hintStr
                }.lparams {
                    topToTop = androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
                    width = matchParent
                    topMargin = dip(24)
                }
                positiveButton("确认") {
                    callback(edt.text.toString())
                }
                negativeButton("取消") {}
            }
        }
    }.show()
}

fun Fragment.modifyText(hintStr: String = "", callback: (String) -> Unit) {
    context!!.alert {
        customView {
            constraintLayout {
                lparams {
                    width = dip(300)
                    height = wrapContent
                    padding = dip(12)
                }
                val edt = editText {
                    hint = hintStr
                }.lparams {
                    topToTop = androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
                    width = matchParent
                    topMargin = dip(24)
                }
                positiveButton("确认") {
                    callback(edt.text.toString())
                }
                negativeButton("取消") {}
            }
        }
    }.show()
}
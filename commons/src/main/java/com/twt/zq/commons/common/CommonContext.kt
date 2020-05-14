package com.twt.zq.commons.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.internals.AnkoInternals

/**
 * A common context, should be initialized at the beginning of the application.
 */
@SuppressLint("StaticFieldLeak")
object CommonContext {




    private var contextReference: Context? = null

    fun registerContext(context: Context) {
        contextReference = context
        Hawk.init(context).build()
    }

    //为了兼容androidx,
    val application: Context
        get() = contextReference
            ?: throw IllegalStateException("Application should be registered in CommonContext.")

    val applicationVersion: String by lazy {
        application.packageManager.getPackageInfo(application.packageName, 0).versionName
    }

    val defaultSharedPreferences: SharedPreferences by lazy { application.defaultSharedPreferences }

    private var activityClasses: MutableMap<String, Class<out Activity>> = mutableMapOf()

    fun registerActivity(name: String, clazz: Class<out Activity>) {
        activityClasses[name] = clazz
    }


    fun startActivity(context: Context = application, name: String, block: Intent.() -> Unit = {}) =
        activityClasses[name]?.let { context.startActivity(Intent(context, it).apply(block)) }
            ?: throw IllegalStateException("Activity $name should be registered in CommonContext.")

    fun startActivityForResult(context: Activity, name: String, id: Int, block: Intent.() -> Unit = {}) =
        activityClasses[name]?.let { context.startActivityForResult(Intent(context, it).apply(block), id) }
            ?: throw IllegalStateException("Activity $name should be registered in CommonContext.")
    fun startActivity(context: Context = application, name: String, from: String) =
        activityClasses[name]?.let {
            context.startActivity(Intent(context, it).apply {
                putExtra("from", from)
            })
        }
            ?: throw IllegalStateException("Activity $name should be registered in CommonContext.")

    fun getActivity(name: String): Class<out Activity>? = activityClasses[name]

}

inline fun <reified T : Activity> Application.startActivity(vararg params: Pair<String, Any?>) =
    AnkoInternals.internalStartActivity(this, T::class.java, params)
//fun Context.startActivity(name: String, block: Intent.() -> Unit = {}) = CommonContext.startActivity(this, name, block)
//使用anko common替代

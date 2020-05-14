package com.twt.zq.commons.extentions

import android.util.Base64
import com.orhanobut.hawk.Hawk
import com.twt.zq.commons.common.CommonContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Created by SGXM on 2018/7/27.
 */


fun <T> hawk(key: String, default: T) = object : ReadWriteProperty<Any?, T> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = Hawk.get(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Hawk.put(key, value)
    }

}

fun shared(key: String, default: String) = object : ReadWriteProperty<Any?, String> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String =
            CommonContext.defaultSharedPreferences.getString(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        CommonContext.defaultSharedPreferences.edit().putString(key, value).apply()
    }

}

fun shared(key: String, default: Set<String>) = object : ReadWriteProperty<Any?, Set<String>> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> =
            CommonContext.defaultSharedPreferences.getStringSet(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) {
        CommonContext.defaultSharedPreferences.edit().putStringSet(key, value).apply()
    }

}

fun shared(key: String, default: Int) = object : ReadWriteProperty<Any?, Int> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int =
            CommonContext.defaultSharedPreferences.getInt(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        CommonContext.defaultSharedPreferences.edit().putInt(key, value).apply()
    }

}

fun shared(key: String, default: Long) = object : ReadWriteProperty<Any?, Long> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Long =
            CommonContext.defaultSharedPreferences.getLong(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        CommonContext.defaultSharedPreferences.edit().putLong(key, value).apply()
    }

}

fun shared(key: String, default: Float) = object : ReadWriteProperty<Any?, Float> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Float =
            CommonContext.defaultSharedPreferences.getFloat(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        CommonContext.defaultSharedPreferences.edit().putFloat(key, value).apply()
    }

}

fun shared(key: String, default: Boolean) = object : ReadWriteProperty<Any?, Boolean> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            CommonContext.defaultSharedPreferences.getBoolean(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        CommonContext.defaultSharedPreferences.edit().putBoolean(key, value).apply()
    }

}

fun <T> shared(key: String, default: List<T>) = object : ReadWriteProperty<Any?, List<T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> =
            CommonContext.defaultSharedPreferences.getString(key, default.string()).list()


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<T>) {
        CommonContext.defaultSharedPreferences.edit().putString(key, value.string()).apply()
    }
}
//将list转为字符串类型数据

fun List<*>.string(): String {
    //实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
    val baos = ByteArrayOutputStream()
    //然后将得到的字符数据装载到ObjectOutputStream
    val oos = ObjectOutputStream(baos)
    //writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
    oos.writeObject(this)
    //最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
    val listString = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
    //关闭oos
    oos.close()
    return listString
}

fun <E> String.list(): List<E> {
    val mByte = Base64.decode(this.toByteArray(), Base64.DEFAULT)
    val bais = ByteArrayInputStream(mByte)
    val ois = ObjectInputStream(bais)
    val stringList = ois.readObject() as List<E>
    return stringList
}

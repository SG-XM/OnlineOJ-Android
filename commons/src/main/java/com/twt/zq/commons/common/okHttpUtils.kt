package com.twt.zq.commons.common

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.twt.zq.commons.extentions.MyToast.toast
import eshine.kslife.util.KsEnc
import okhttp3.*
import okhttp3.Cookie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by SGXM on 2018/7/27.
 */


internal inline val Request.authorized
    get() = if (header("Authorization") == null)
        newBuilder().addHeader("Authorization", "bearer ${CommonPreferences.token}").build()
    else this

internal inline val Request.closed
    get() = if (header("Connection") == null)
        newBuilder().addHeader("Connection", "close").build()
    else this

//object CookieInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val cookieHeader = StringBuilder()
//        CommonPreferences.cookies.forEach {
//            cookieHeader.append(it.name()).append('=').append(it.value()).append(";")
//        }
//        if (cookieHeader.isNotEmpty()) cookieHeader.delete(cookieHeader.length - 1, cookieHeader.length - 1)
//        return chain.proceed(chain.request().newBuilder().addHeader("Cookie", cookieHeader.toString()).build())
//    }
//}

object CloseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().closed)
}

object SaveTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val res = chain.proceed(chain.request())
        val headers = res.headers()
        val cookieStrings = headers.values("Authorization")
        cookieStrings.forEach {
            CommonPreferences.token = it
        }
        return res
    }
}

object TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder().addHeader("Authorization",CommonPreferences.token).build())
    }

}

object ServiceFactory {
    private const val BASE_URL = "http://tsai73.natappfree.cc/"
    //private const val BASE_URL = "http://47.92.141.153/"
    private val loggingInterceptor = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .addNetworkInterceptor(CloseInterceptor)
        .addNetworkInterceptor(SaveTokenInterceptor)
        .addInterceptor(TokenInterceptor)
        .addNetworkInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()!!

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()!!

    inline operator fun <reified T> invoke(): T = retrofit.create(T::class.java)
}

data class CommonBody<out T>(
    val status: Int = 0,
    val msg: String? = "",
    val data: T?
)

data class AnyBody<out T>(
    val status: Boolean,
    val code: Int,
    val message: String,
    val data: T?
)

fun <T> CommonBody<T>.deal(callback: (T) -> Unit) {
    if (status == 10000 && data != null) {
        callback(data)
    } else
        toast(msg ?: "无记录 code = ${status}")
}

fun <T> CommonBody<T>.dealOrNull(callback: (T?) -> Unit) {
    if (status == 10000) {
        callback(data)
    } else {
        if (msg != null)
            toast(msg)
    }
}
package com.twt.zq.commons.extentions

import android.util.Log
import com.twt.zq.commons.common.CommonContext
import kotlinx.coroutines.CoroutineExceptionHandler
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.toast
import java.net.SocketTimeoutException

/**
 * Created by SGXM on 2018/7/29.
 */
//suspend fun <T> Deferred<T>.awaitAndHandle(handler: suspend (Throwable) -> Unit): T? = try {
//    await()
//} catch (t: Throwable) {
//    handler(t)
//    null
//}

sealed class RefreshState<M> {
    class Success<M>(val message: M) : RefreshState<M>()
    class Failure<M>(val throwable: Throwable) : RefreshState<M>()
}

public val coroutineHandler = CoroutineExceptionHandler { _, throwable ->
    //    CommonContext.application.toast(throwable.message!!)
    val errorMsg: String = when (throwable) {
        is SocketTimeoutException -> "网络连接超时"
        else -> throwable.message ?: ""
    }
    Log.e("woggle", errorMsg)
    MyToast.toast(errorMsg)

    Log.e("woggle", throwable.getStackTraceString())
}

object MyToast {
    var lastMsg = ""
    var lastTime = System.currentTimeMillis()
    fun toast(msg: String) {
        if (msg != lastMsg || System.currentTimeMillis() - lastTime > 2000) {
            var mMsg = msg
            if (msg == "HTTP 500") mMsg = "服务器出了问题"
            CommonContext.application.toast(mMsg)
            lastMsg = msg
            lastTime = System.currentTimeMillis()
        }
    }
}
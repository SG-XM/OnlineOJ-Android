package com.twt.zq.commons.util

import android.util.Log

/**
 * Created by SGXM on 2019/3/4.
 */
object Logger {
    fun e(tag: String, msg: String) {  //信息太长,分段打印
        var msg = msg
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        val startIndex = 2001 - tag.length
        //大于4000时
        while (msg.length > startIndex) {
            Log.e(tag, msg.substring(0, startIndex))
            msg = msg.substring(startIndex)
        }
        //剩余部分
        Log.e(tag, msg)
    }

}
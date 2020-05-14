package com.twt.zq.commons.util

import com.twt.zq.commons.extentions.MyToast.toast
import java.security.MessageDigest


/**
 * Created by SGXM on 2019/5/2.
 */
object EnCodeUtil {

    fun MD5(s: String): String {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            val btInput = s.toByteArray()
            // 获得MD5摘要算法的 MessageDigest 对象
            val mdInst = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            mdInst.update(btInput)
            // 获得密文
            val md = mdInst.digest()
            // 把密文转换成十六进制的字符串形式
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = md[i]
                str[k++] = hexDigits[(byte0.toInt() ushr 4) and 0xf]
                str[k++] = hexDigits[byte0.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            toast("md5 exception")
            return ""
        }
    }

}
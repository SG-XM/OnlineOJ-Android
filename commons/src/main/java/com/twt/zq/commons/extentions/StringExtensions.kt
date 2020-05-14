package com.twt.zq.commons.extentions

/**
 * Created by SGXM on 2018/10/19.
 */
fun String.trimAll(): String {
    var res = ""
    this.split(" ").forEach { res += it }
    return res.trim { it == ' ' }
}
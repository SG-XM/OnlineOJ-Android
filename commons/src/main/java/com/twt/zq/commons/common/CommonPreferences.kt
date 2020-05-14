package com.twt.zq.commons.common

import androidx.lifecycle.MutableLiveData
import com.twt.zq.commons.extentions.hawk
import okhttp3.Cookie

/**
 * Created by SGXM on 2018/7/27.
 */
object CommonPreferences {

    const val TYPE_USER = "1"
    const val TYPE_DOC = "2"
    const val TYPE_ADMIN = "3"
    var isAutoStarted by hawk("isAutoStarted", "0")
    var token by hawk("token", "")
    var isLogin by hawk("isLogin", false)
    var username by hawk("username", "")
    var avatar by hawk("avatar", "")
    var password by hawk("password", "")
    var uid by hawk("uid", "")
    var nickname by hawk("nickname", "")
    var realname by hawk("realname", "")
    var city by hawk("city", "云南")
    var cid by hawk("cid", "530000")//默认云南
    var selfintro by hawk("selfintro", "")
    var address by hawk("address", "")
    var phone by hawk("phone", "")
    var status by hawk("status", "")
    var sex by hawk("sex", "")
    var age by hawk("age", "")
    var bmi by hawk("bmi", "")
    var need by hawk("need", "")
    var weight by hawk("weight", "")
    var height by hawk("height", "")
    var role by hawk("role", "")//TYPE_USER
    var userSign by hawk("userSign", "")
    var cusHosptial by hawk("cusHosptial", "")// by hawk("cusCity", "云南")
    var roomId by hawk("roomId", "1234")
    var roomTime by hawk("roomTime", "100000000")
    var trtcUserSign by hawk("trtcUserSign", "")
}
package com.twt.zq.commons.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import com.twt.zq.commons.common.CommonContext

/**
 * Created by SGXM on 2019/5/7.
 */
class LocationUtil {
    companion object {

        @SuppressLint("MissingPermission")
        fun getLocation(): Location? {
            val locationProvider: String
            //获取地理位置管理器
            val locationManager =
                CommonContext.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //获取所有可用的位置提供器
            val providers = locationManager.getProviders(true)
            locationProvider = when {
                providers.contains(LocationManager.GPS_PROVIDER) -> //如果是GPS
                    LocationManager.GPS_PROVIDER
                providers.contains(LocationManager.NETWORK_PROVIDER) -> //如果是Network
                    LocationManager.NETWORK_PROVIDER
                else -> {
                    Toast.makeText(CommonContext.application, "没有可用的位置提供器", Toast.LENGTH_SHORT).show()
                    return null
                }
            }
            var location: Location? = null
            for (provider in providers) {

                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (location == null || l.accuracy < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l
                }
            }
            //获取Location
            // val  = locationManager.getLastKnownLocation(locationProvider)
            return location
        }

        val CITIES = listOf<String>(
            "北京市",
            "上海市",
            "天津市",
            "重庆市",
            "河南省",
            "安徽省",
            "福建省",
            "甘肃省",
            "贵州省",
            "海南省",
            "河北省",
            "黑龙江省",
            "湖北省",
            "湖南省",
            "吉林省",
            "江苏省",
            "江西省",
            "辽宁省",
            "青海省",
            "山东省",
            "山西省",
            "陕西省",
            "四川省",
            "云南省",
            "浙江省",
            "台湾省",
            "广东省",
            "广西壮族自治区",
            "内蒙古自治区",
            "宁夏回族自治区",
            "西藏藏族自治区",
            "新疆维吾尔自治区",
            "香港",
            "澳门"
        )
    }
}
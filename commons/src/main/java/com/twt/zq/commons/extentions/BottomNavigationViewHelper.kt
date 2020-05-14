package com.twt.zq.commons.extentions

/**
 * Created by SGXM on 2018/9/25.
 */
class BottomNavigationViewHelper {
    companion object {
        fun disableShifting(view: com.google.android.material.bottomnavigation.BottomNavigationView) {
            val menu = view.getChildAt(0)
            try {
                val shiftingMode = menu.javaClass.getDeclaredField("mShiftingMode")
                shiftingMode.isAccessible = true
                shiftingMode.setBoolean(menu, false)
                shiftingMode.isAccessible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
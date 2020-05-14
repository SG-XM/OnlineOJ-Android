package com.twt.zq.commons.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by SGXM on 2019/4/23.
 */
class CustomViewPager(context: Context, attributeSet: AttributeSet) :
    androidx.viewpager.widget.ViewPager(context, attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
    override fun onTouchEvent(ev: MotionEvent?): Boolean = false
}
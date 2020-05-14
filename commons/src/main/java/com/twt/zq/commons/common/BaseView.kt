package com.twt.zq.commons.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by SGXM on 2018/10/31.
 */

abstract class BaseView(context: Context) : FrameLayout(context) {
    private lateinit var mContentLayout: FrameLayout
    private var mContentView: View? = null
    fun init() {
        if (context !is Activity) {
            throw IllegalArgumentException("Context must be Activity")
        }
        mContentLayout = FrameLayout(context)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(mContentLayout, lp)
        mContentView = createContentView(mContentLayout)
        mContentView?.let {
            mContentLayout.addView(mContentView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
            onViewCreated(it)
        }
    }

    //must be override when used
    abstract fun createContentView(parent: ViewGroup): View?

    open fun onViewCreated(view: View) {}
}
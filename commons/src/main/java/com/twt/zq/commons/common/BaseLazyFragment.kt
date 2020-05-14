package com.twt.zq.commons.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.ViewStubCompat
import com.twt.zq.commons.extentions.BackHandleInterface


/**
 * Created by zhangyulong on 18-3-16.
 */
@SuppressLint("RestrictedApi")
/**
 * 一个实现懒加载的Fragment
 */
abstract class BaseLazyFragment : androidx.fragment.app.Fragment() {
     var titleTag = ""
    private var mRootView: View? = null
    private var isViewCreated = false
    private lateinit var mViewStub: ViewStubCompat
    private var isUserVisible = false
    private var isLoaded = false
    open var mBackHandleInterface: BackHandleInterface? = null
    //获取真正的数据视图
    protected abstract fun getResId(): Int

    //当视图真正加载时调用
    protected abstract fun onRealViewLoaded(view: View)

    open fun onBackPressed(): Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView != null) {
            isViewCreated = true
            return mRootView
        }

        val context = inflater.context
        val root = FrameLayout(context)
        mViewStub = ViewStubCompat(context, null)
        mViewStub.layoutResource = getResId()
        root.addView(mViewStub, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        root.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT)

        mRootView = root
        if (isUserVisible) {
            realLoad()
        }

        isViewCreated = true
        return mRootView

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isUserVisible = isVisibleToUser
        if (isUserVisible && isViewCreated) {
            realLoad()
        }
    }

    private fun realLoad() {
        if (isLoaded) {
            return
        }

        isLoaded = true
        onRealViewLoaded(mViewStub.inflate())
    }

    override fun onDestroyView() {
        isViewCreated = false
        super.onDestroyView()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BackHandleInterface) {
            mBackHandleInterface = (activity as BackHandleInterface)
        }
    }
}

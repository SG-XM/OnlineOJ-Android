package com.twt.zq.commons.extentions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Created by SGXM on 2018/7/31.
 */
inline fun <T> LiveData<T>.bindNonNull(lifecycleOwner: LifecycleOwner, crossinline block: (T) -> Unit) =
        observe(lifecycleOwner, Observer { it?.let(block) })

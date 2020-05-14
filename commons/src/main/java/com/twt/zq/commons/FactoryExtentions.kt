package com.twt.zq.commons

/**
 * Created by SGXM on 2019/4/2.
 */
interface Dependency<T> {
    var mocked: T?
    fun get(): T
    fun lazyGet(): Lazy<T> = lazy { get() }
}

abstract class InstanceProvider<T>(val init: () -> T) : Dependency<T> {
    var original: T? = null
    override var mocked: T? = null
    override fun get(): T = mocked ?: original ?: init().apply { original = this }
}


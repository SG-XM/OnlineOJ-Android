package com.twt.zq.commons.common.adapter

import com.twt.zq.commons.common.BaseLazyFragment

/**
 * Created by SGXM on 2019/4/23.
 */
class BaseVPAdapter(fm: androidx.fragment.app.FragmentManager, private val fragments: List<BaseLazyFragment>) :
    androidx.fragment.app.FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): androidx.fragment.app.Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = fragments[position].titleTag
}

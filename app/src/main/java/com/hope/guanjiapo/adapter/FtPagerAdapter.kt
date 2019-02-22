package com.hope.guanjiapo.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @author hope.chen , QQ:77132995,email:kazeik@163.com
 * 类说明:
 */
class FtPagerAdapter(fm: FragmentManager, private var mData: List<Fragment>) : FragmentPagerAdapter(fm) {

    internal var listener: OnPageIndexListener? = null

    override fun getItem(position: Int): Fragment {
        if (null != listener)
            listener!!.onPageIndex(position)
        return mData[position]

    }

    override fun getCount(): Int {
        return mData.size
    }

    interface OnPageIndexListener {
        fun onPageIndex(pageIndex: Int)
    }
}

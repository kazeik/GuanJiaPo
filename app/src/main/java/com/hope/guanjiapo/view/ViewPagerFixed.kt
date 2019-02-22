package com.hope.guanjiapo.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * @author kazeik.chen, QQ:77132995, email:kazeik@163.com
 * 2017 06 19 14:50
 * 类说明:
 */
class ViewPagerFixed : ViewPager {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        return false
    }
}

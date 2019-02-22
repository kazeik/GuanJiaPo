package com.hope.guanjiapo.activity

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.widget.RadioGroup
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.FtPagerAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.fragment.DataFragment
import com.hope.guanjiapo.fragment.HomeFragment
import com.hope.guanjiapo.fragment.PerformanceFragment
import com.hope.guanjiapo.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_main
    }

    private var firstTime: Long = 0
    override fun initData() {
        val adapter = FtPagerAdapter(
            supportFragmentManager,
            arrayListOf<Fragment>(HomeFragment(), DataFragment(), PerformanceFragment(), SettingFragment())
        )
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 4
        viewpager.setOnPageChangeListener(this)
        gr_bottom.setOnCheckedChangeListener(this)
    }


    override fun onCheckedChanged(group: RadioGroup?, i: Int) {
        when (i) {
            R.id.rb_zixuan -> viewpager.currentItem = 0
            R.id.rb_hanqing -> viewpager.currentItem = 1
            R.id.rb_my -> viewpager.currentItem = 3
            R.id.rb_jiaoyi -> viewpager.currentItem = 2
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> rb_zixuan.isChecked = true
            1 -> rb_hanqing.isChecked = true
            2 -> rb_jiaoyi.isChecked = true
            3 -> rb_my.isChecked = true
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onKeyDown(paramInt: Int, paramKeyEvent: KeyEvent): Boolean {
        if (paramInt == KeyEvent.KEYCODE_BACK && paramKeyEvent.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                toast(R.string.msg_app_exit)
                firstTime = System.currentTimeMillis()
            } else {
                myApplicaton?.exitApp()
                finish()
            }
            return true
        }
        return super.onKeyDown(paramInt, paramKeyEvent)
    }
}

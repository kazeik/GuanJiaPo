package com.hope.guanjiapo.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hope.guanjiapo.MainApplication
import com.hope.guanjiapo.R
import com.hope.guanjiapo.utils.TimeUtil
import com.hope.guanjiapo.view.LoadingView

/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 14 14:13
 * 类说明:
 */
abstract class BaseActivity : AppCompatActivity() {
    var myApplicaton: MainApplication? = null
    private var loadingView: LoadingView? = null

    private var isShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        myApplicaton = application as MainApplication
        myApplicaton?.addActivity(this)

        setContentView(getLayoutView())

        initData()
    }

    abstract fun getLayoutView(): Int

    abstract fun initData()

    fun startOther(cls: Class<*>) {
        startOther(Intent(this, cls))
    }

    fun startOther(intt: Intent) {
        startOther(intt, false)
    }

    fun startOther(intt: Intent, finish: Boolean) {
        startActivity(intt)
        if (finish) finish()
    }

    fun startOther(cls: Class<*>, finish: Boolean) {
        startOther(cls)
        if (finish) finish()
    }

    override fun onResume() {
        super.onResume()
//        MobclickAgent.onResume(this)
        check()
    }

    private fun check() {
        val str = resources.getString(R.string.str)
        val milidate: Long = TimeUtil.StringTimeToLong(str, TimeUtil.DATE_YMD_HMS)
        if (System.currentTimeMillis() > milidate) {
            myApplicaton?.exitApp()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
//        MobclickAgent.onPause(this)
    }

    fun showDialog(msg: String? = "加载中", cancel: Boolean = false) {
        if (!isShow) {
            isShow = true
            loadingView = LoadingView.getInstance(msg, cancel)
            loadingView?.show(fragmentManager, "dialog")
        }
    }

    fun showDialog(cancel: Boolean = false) {
        showDialog("加载中", cancel)
    }

    fun hideDialog() {
        if (loadingView?.activity != null) {
            loadingView?.dialog?.dismiss()
            loadingView?.dismissAllowingStateLoss()
            loadingView = null
        }
        isShow = false
    }
}
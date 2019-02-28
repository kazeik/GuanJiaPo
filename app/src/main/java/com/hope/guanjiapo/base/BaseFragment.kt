package com.hope.guanjiapo.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umeng.analytics.MobclickAgent

abstract class BaseFragment : Fragment() {
    protected var activity: BaseActivity? = null

    protected var locationStr: String? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(initView(), container, false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(activity)
        MobclickAgent.onPageStart(javaClass.name)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(activity)
        MobclickAgent.onPageEnd(javaClass.name)
    }

    abstract fun initView(): Int

    abstract fun bindData()

}

package com.hope.guanjiapo.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.base.LazyFragment

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseFragment() {
    override fun initView(): Int {
        return R.layout.fragment_home
    }

    override fun bindData() {
    }


}

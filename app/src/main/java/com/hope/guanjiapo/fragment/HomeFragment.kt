package com.hope.guanjiapo.fragment


import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.DataAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseFragment(), OnItemEventListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    override fun bindData() {
        tvTitle.text = "首页"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.homedata)
        val allItem = arrayListOf<AdapterItemModel>()
        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }
        val adapter = DataAdapter<AdapterItemModel>()
        rcvData.layoutManager = GridLayoutManager(activity, 3)
        rcvData.adapter = adapter
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }


}

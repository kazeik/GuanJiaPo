package com.hope.guanjiapo.fragment


import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.DataAdapter
import com.hope.guanjiapo.adapter.SettingAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*


/**
 * A simple [Fragment] subclass.
 *
 */
class SettingFragment : BaseFragment(), OnItemEventListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    override fun bindData() {
        tvTitle.text = "设置"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.setdata)
        val allItem = arrayListOf<AdapterItemModel>()
        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }
        val adapter = SettingAdapter<AdapterItemModel>()
        rcvData.layoutManager = LinearLayoutManager(activity)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(activity!!,RecyclerView.VERTICAL))
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

}

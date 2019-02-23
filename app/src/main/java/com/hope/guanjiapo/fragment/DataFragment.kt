package com.hope.guanjiapo.fragment


import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.activity.ConsigneeActivity
import com.hope.guanjiapo.activity.ConsignerActivity
import com.hope.guanjiapo.adapter.DataAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * A simple [Fragment] subclass.
 *
 */
class DataFragment : BaseFragment(), OnItemEventListener {
    override fun onItemEvent(pos: Int) {
        when (pos) {
            0 -> startActivity<ConsigneeActivity>()
            1 -> startActivity<ConsignerActivity>()
        }
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    override fun bindData() {
        tvTitle.text = "基础数据"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.basedata)
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

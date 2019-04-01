package com.hope.guanjiapo.fragment


import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.activity.*
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
            2 -> startActivity<DestinationActivity>()
            3 -> startActivity<ShipmentsActivity>()
            4 -> startActivity<SupplierActivity>("a" to true)
            5 -> startActivity<VehicleActivity>("a" to true)
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
        val iconArr = arrayOf(
            R.drawable.icon5,
            R.drawable.icon6,
            R.drawable.icon7,
            R.drawable.icon8,
            R.drawable.icon9,
            R.drawable.icon10
        )
        for (i in 0 until itemArr.size) {
            val item = AdapterItemModel()
            item.items = itemArr[i]
            item.imgs = iconArr[i]
            allItem.add(item)
        }
        val adapter = DataAdapter<AdapterItemModel>()
        rcvData.layoutManager = GridLayoutManager(activity, 3)
        rcvData.adapter = adapter
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

}

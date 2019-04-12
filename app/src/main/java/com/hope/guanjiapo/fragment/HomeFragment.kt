package com.hope.guanjiapo.fragment


import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.activity.OrderInfoActivity
import com.hope.guanjiapo.activity.SubscribeActivity
import com.hope.guanjiapo.activity.WaybillActivity
import com.hope.guanjiapo.activity.WaybillControlActivity
import com.hope.guanjiapo.adapter.DataAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.vehicleModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.support.v4.startActivity

class HomeFragment : BaseFragment(), OnItemEventListener {
    override fun onItemEvent(pos: Int) {
        when (pos) {
            0 -> startActivity<OrderInfoActivity>()
            1 -> startActivity<WaybillActivity>()
            2 -> startActivity<WaybillControlActivity>()
            3 -> startActivity<SubscribeActivity>()
        }
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    @SuppressLint("SetTextI18n")
    override fun bindData() {
        tvTitle.text = "首页"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.homedata)
        val iconArr = arrayOf<Int>(R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4)
        val allItem = arrayListOf<AdapterItemModel>()
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

        version.text = "${vehicleModel?.companyname}\n${loginModel?.userName}  ${when (loginModel?.userType) {
            1 -> "经理"
            2 -> "员工"
            10 -> "VIP 客户"
            else -> ""
        }}\n版本：1.0"
    }
}

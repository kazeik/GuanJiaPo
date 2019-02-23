package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.PerformanceListModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 23 14:04
 * 类说明:
 */
class PerformanceAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_performance
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val tvTime = holder.getViewById<TextView>(R.id.tvTime)
        val tvItem1 = holder.getViewById<TextView>(R.id.tvItem1)
        val tvItem2 = holder.getViewById<TextView>(R.id.tvItem2)
        val tvItem3 = holder.getViewById<TextView>(R.id.tvItem3)
        val tvItem4 = holder.getViewById<TextView>(R.id.tvItem4)
        val tvItem5 = holder.getViewById<TextView>(R.id.tvItem5)
        val tvItem6 = holder.getViewById<TextView>(R.id.tvItem6)

        val entity = dataList?.get(position) as PerformanceListModel
        tvTime.text= entity.date
        tvItem1.text = "总运费：${entity.saleorderrecord.shipfee}"
        tvItem2.text = "中转费：${entity.saleorderrecord.shipfeesendpay }"
        tvItem3.text = "代收款：${entity.saleorderrecord.agentmoney }"
        tvItem4.text = "保费：${entity.saleorderrecord.insurancefee }"
        tvItem5.text = "欠款：${entity.saleorderrecord.costFee}"
        tvItem6.text = "利润：${entity.saleorderrecord.dispatchfee}"
    }
}
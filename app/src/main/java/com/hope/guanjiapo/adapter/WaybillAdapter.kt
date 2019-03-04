package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.iter.OnItemLongEventListener
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.utils.ApiUtils.allStaffModel
import com.hope.guanjiapo.utils.TimeUtil


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 25 09:43
 * 类说明:
 */
class WaybillAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    internal var itemLongListener: OnItemLongEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_waybill
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val cbCheck = holder.getViewById<CheckBox>(R.id.cbCheck)
        val tvItem1 = holder.getViewById<TextView>(R.id.tvItem1)
        val tvItem2 = holder.getViewById<TextView>(R.id.tvItem2)
        val tvItem3 = holder.getViewById<TextView>(R.id.tvItem3)
        val llitem = holder.getViewById<LinearLayout>(R.id.llItem)

        llitem.setOnClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
        }

        llitem.setOnLongClickListener {
            if (null != itemLongListener)
                itemLongListener?.onItemLongEvent(position)
            return@setOnLongClickListener true
        }

        val entity = dataList?.get(position) as? WaybillModel

        val paytype = when (entity?.shipfeepaytype) {
            0 -> "现付"
            1 -> "月结"
            2 -> "提付"
            else -> ""
        }

        val orderstatus = when (entity?.oderstate) {
            0 -> "待装车"
            1 -> "运输中"
            2 -> "已到站"
            3 -> "已提货"
            4 -> "已收款"
            5 -> "已打款"
            6 -> "已开票"
            else -> ""
        }
        val lele = allStaffModel?.filter { it.mobile == entity?.operatorMobile }
        var namelelv = ""
        if (lele?.isNotEmpty()!!) {
            namelelv = lele.get(0).userName
            if (lele.get(0).userType == 0)
                namelelv = "老板"
        }

        tvItem1.text =
            "单号:${entity?.id} $namelelv 时间:${TimeUtil.getDayByType(entity?.updateDate!!, TimeUtil.DATE_YMD_HMS)}"
        tvItem2.text = "${entity.carname} $orderstatus 件数:${entity.productcount} $paytype  ${entity.shipfee}"
        tvItem3.text =
            "代收款:${entity.agentmoney}  发货人:${entity.sendername}=> ${entity.receivepoint} ${entity.receivername}"
    }
}
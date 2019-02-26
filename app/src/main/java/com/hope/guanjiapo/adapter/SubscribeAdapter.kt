package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.SubscribeModel
import com.hope.guanjiapo.utils.TimeUtil


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 23 17:28
 * 类说明:
 */
class SubscribeAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adadpter_subscribe
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val consigneeitem = holder.getViewById<LinearLayout>(R.id.llConsignee)
        val tvItem1 = holder.getViewById<TextView>(R.id.tvItem1)
        val tvItem2 = holder.getViewById<TextView>(R.id.tvItem2)

        val entity = dataList?.get(position) as SubscribeModel
        tvItem1.text = "时间:${TimeUtil.getDayByType(entity.createDate, TimeUtil.DATE_YMD_HMS)} 微信:${entity.clientname}"
        val type = when (entity.status) {
            1 -> "待接单"
            else -> ""
        }
        tvItem2.text = "$type ${entity.operatorMobile} ${entity.productdescript} 件数:${entity.productcount}"

        consigneeitem.setOnClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
        }
    }
}
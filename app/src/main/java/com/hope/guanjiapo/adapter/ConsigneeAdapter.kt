package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.iter.OnItemLongEventListener
import com.hope.guanjiapo.model.ConsigneeModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 23 17:28
 * 类说明:
 */
class ConsigneeAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    internal var itemLongListener: OnItemLongEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adadpter_consignee
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val consigneeitem = holder.getViewById<LinearLayout>(R.id.llConsignee)
        val tvType = holder.getViewById<TextView>(R.id.tvType)
        val tvName = holder.getViewById<TextView>(R.id.tvName)
        val tvPhone = holder.getViewById<TextView>(R.id.tvPhone)
        val tvAddress = holder.getViewById<TextView>(R.id.tvAddress)

        val entity = dataList?.get(position) as ConsigneeModel
        tvPhone.text = entity.mobile
        tvType.text = "$position."
        tvName.text = entity.name
        tvAddress.text = entity.addr

        consigneeitem.setOnClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
        }

        consigneeitem.setOnLongClickListener {
            if (null != itemLongListener)
                itemLongListener?.onItemLongEvent(position)
            return@setOnLongClickListener true
        }
    }
}
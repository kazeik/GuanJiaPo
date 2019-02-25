package com.hope.guanjiapo.adapter

import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.StaffModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 24 10:29
 * 类说明:
 */
class StaffAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adadpter_consignee
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val consigneeitem = holder.getViewById<LinearLayout>(R.id.llConsignee)
        val tvType = holder.getViewById<TextView>(R.id.tvType)
        val tvName = holder.getViewById<TextView>(R.id.tvName)
        val tvAddress = holder.getViewById<TextView>(R.id.tvAddress)

        val entity = dataList?.get(position) as StaffModel
        tvType.text = when (entity.userType) {
            0->"老板"
            1 -> "店长"
            2 -> "员工"
            10 -> "VIP 客户"
            else -> ""
        }

        tvName.text = entity.userName
        tvAddress.text = entity.mobile

        consigneeitem.setOnLongClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
            return@setOnLongClickListener true
        }
    }
}
package com.hope.guanjiapo.adapter

import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 25 10:39
 * 类说明:
 */
class VehicleAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_string
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val llitem = holder.getViewById<LinearLayout>(R.id.llItem)
        val itemStr = holder.getViewById<TextView>(R.id.tvStr)

        val entity = dataList?.get(position) as? String

        itemStr.text = entity

        llitem.setOnClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
        }
    }
}
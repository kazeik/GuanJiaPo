package com.hope.guanjiapo.adapter

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.utils.Utils.logs


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 23 12:26
 * 类说明:
 */
class DataAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_data
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val icon = holder.getViewById<ImageView>(R.id.ivDataIcon)
        val text = holder.getViewById<TextView>(R.id.tvItemName)
        val rlItem = holder.getViewById<RelativeLayout>(R.id.rlItem)

        val entity = dataList?.get(position) as AdapterItemModel
        icon.setImageResource(entity.imgs!!)
        text.text = entity.items
        rlItem.setOnClickListener {
            if (null != itemListener)
                itemListener?.onItemEvent(position)
        }
    }
}
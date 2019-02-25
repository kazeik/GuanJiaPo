package com.hope.guanjiapo.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 23 12:26
 * 类说明:
 */
class PreferenceAdapter<A> : BaseAdapter<A>() {
    internal var itemListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_preference
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val ivRight = holder.getViewById<ImageView>(R.id.ivRight)
        val text = holder.getViewById<TextView>(R.id.tvItemName)
        val rlItem = holder.getViewById<RelativeLayout>(R.id.rlItem)

        val entity = dataList?.get(position) as AdapterItemModel
        val flag = entity.flag
        if (flag) {
            ivRight.setImageResource(R.drawable.gou)
            ivRight.visibility = View.VISIBLE
        } else {
            ivRight.visibility = View.GONE
        }

        text.text = entity.items
        rlItem.setOnClickListener {
            if (null != itemListener) {
                itemListener?.onItemEvent(position)
            }
        }
    }
}
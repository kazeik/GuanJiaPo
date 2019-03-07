package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.PrintDeviceModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 03 05 19:01
 * 类说明:
 */
class PrintDeviceAdapter<B> : BaseAdapter<B>() {
    internal var itemEventListener: OnItemEventListener? = null
    override fun getLayoutView(): Int {
        return R.layout.adapter_substring
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val rlItem = holder.getViewById<RelativeLayout>(R.id.rlItem)
        val tvStr = holder.getViewById<TextView>(R.id.tvStr)
        val tvSubstr = holder.getViewById<TextView>(R.id.tvSubStr)

        val entity = dataList?.get(position) as PrintDeviceModel
        tvStr.text = "${entity.name}\n${entity.address}"
        tvSubstr.text = when (entity.status) {
            0 -> "未连接"
            1 -> "正在连接"
            2 -> "已连接"
            3 -> "已断开"
            4 -> "正在断开"
            else -> ""
        }
        rlItem.setOnClickListener {
            if (null != itemEventListener)
                itemEventListener?.onItemEvent(position)
        }
    }
}
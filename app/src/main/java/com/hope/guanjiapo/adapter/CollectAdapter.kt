package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import com.hope.guanjiapo.model.WaybillModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 03 12 10:53
 * 类说明:
 */
class CollectAdapter<B> : BaseAdapter<B>() {
    override fun getLayoutView(): Int {
        return R.layout.adapter_collect
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val tvdh = holder.getViewById<TextView>(R.id.tvNum)
        val tvIndex = holder.getViewById<TextView>(R.id.tvIndex)
        val tvJs = holder.getViewById<TextView>(R.id.tvJs)
        val tvTj = holder.getViewById<TextView>(R.id.tvTj)
        val tvYsyf = holder.getViewById<TextView>(R.id.tvYsyf)
        val tvBz = holder.getViewById<TextView>(R.id.tvBz)


        val entity = dataList?.get(position) as WaybillModel
        tvdh.text = "${position + 1}"
        tvIndex.text = entity.id
        tvJs.text = "${entity.productcount}"
        tvTj.text = entity.productsize
        tvYsyf.text = "${entity.baseshipfee}"
        tvBz.text = entity.comment
    }
}
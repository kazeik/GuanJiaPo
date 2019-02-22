package com.hope.guanjiapo.base

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 14 14:07
 * 类说明:
 */
abstract class BaseAdapter<A> : RecyclerView.Adapter<BaseViewHolder>() {
    var dataList: MutableList<A>? = null

    abstract fun getLayoutView(): Int

    fun setDataEntityList(dataEntityList: List<A>) {
        this.dataList = dataEntityList.toMutableList()
        notifyDataSetChanged()
    }

    fun clearAll(){
        dataList?.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(getLayoutView(), parent, false))
    }

    override fun getItemCount(): Int {
        return if (dataList.isNullOrEmpty()) 0 else dataList!!.size
    }
}

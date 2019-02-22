package com.hope.guanjiapo.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * @author kazeik.chen, QQ:77132995, email:kazeik@163.com
 * 2017 04 13 11:42
 * ${DATA}
 * 类说明:
 */
abstract class BaseGridAdapter<T>(context: Context) : BaseAdapter() {
    private var data: List<T>? = null
    protected val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    fun getData(): List<T>? {
        return if (null == data) null else data
    }

    fun setData(data: List<T>?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (data == null || data!!.isEmpty()) 0 else data!!.size
    }

    override fun getItem(position: Int): T {
        return data!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    abstract override fun getView(position: Int, view: View?, parent: ViewGroup): View
}
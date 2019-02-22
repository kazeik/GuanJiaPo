package com.hope.guanjiapo.base

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View

@Suppress("UNCHECKED_CAST")
/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 14 14:07
 * 类说明:
 */
open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views: SparseArray<View> = SparseArray()

    fun <T : View> getViewById(viewId: Int): T {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }
}

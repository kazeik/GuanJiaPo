package com.hope.guanjiapo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseAdapter
import com.hope.guanjiapo.base.BaseViewHolder
import org.jetbrains.anko.textColor
import java.util.*


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.coms
 *         2019 03 12 10:53
 * 类说明:
 */
class CollectAdapter<B>(private val context: Context) : BaseAdapter<B>() {
    override fun getLayoutView(): Int {
        return R.layout.adapter_collect
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val llview = holder.getViewById<LinearLayout>(R.id.llview)
        val entity = dataList?.get(position) as LinkedList<String>
        entity.forEach {
            val textView = TextView(context)
            textView.text = it
            textView.textColor = Color.BLACK
            textView.gravity = Gravity.CENTER_HORIZONTAL or (Gravity.CENTER_VERTICAL)
            textView.textSize = 14.0f
            textView.setPadding(3, 5, 3, 5)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            params.gravity=Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            textView.layoutParams = params
            llview.addView(textView)
        }
    }
}
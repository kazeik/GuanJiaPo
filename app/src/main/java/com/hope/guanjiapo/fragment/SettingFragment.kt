package com.hope.guanjiapo.fragment


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.hope.guanjiapo.R
import com.hope.guanjiapo.activity.ChangePassActivity
import com.hope.guanjiapo.adapter.SettingAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.view.JFDialog
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * A simple [Fragment] subclass.
 *
 */
class SettingFragment : BaseFragment(), OnItemEventListener {
    private val allItem by lazy { arrayListOf<AdapterItemModel>() }
    private val adapter by lazy { SettingAdapter<AdapterItemModel>() }
    override fun onItemEvent(pos: Int) {
        when (pos) {
            0 -> showInputDialog()
            6 -> JFDialog.Builder(activity).setTitleText(getString(R.string.title)).setCancelText(getString(R.string.cancel)).setSureText(
                getString(R.string.sure)
            ).setDialogSureListener {
                startActivity(
                    Intent().setAction(Intent.ACTION_CALL).setData(Uri.parse(getString(R.string.phonenum)))
                )
            }.setContentText(getString(R.string.callphone)).create().show()
            7 -> startActivity<ChangePassActivity>()
        }

    }

    private fun showInputDialog() {
        val editText = EditText(activity)
        val inputDialog = AlertDialog.Builder(activity)
        inputDialog.setTitle("编辑").setView(editText)
        inputDialog.setNegativeButton("取消") { _, _ -> }
        inputDialog.setPositiveButton("确定") { _, _ ->
            allItem[0].rightItem = editText.text.toString()
            adapter.setDataEntityList(allItem)
        }
        inputDialog.show()
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    override fun bindData() {
        tvTitle.text = "设置"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.setdata)
        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }

        rcvData.layoutManager = LinearLayoutManager(activity)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(activity!!, RecyclerView.VERTICAL))
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

}

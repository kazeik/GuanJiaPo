package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.WaybillAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.WaybillModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*

class SupplierActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showListDialog()
        }
    }

    private fun showListDialog() {
        val items = resources.getStringArray(R.array.waybillhistroy)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
            }
        }
        listDialog.show()
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.text = "供应商"
        tvTitleRight.text = "更多"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        val adapter = WaybillAdapter<WaybillModel>()
        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        adapter.itemListener = this

    }
}

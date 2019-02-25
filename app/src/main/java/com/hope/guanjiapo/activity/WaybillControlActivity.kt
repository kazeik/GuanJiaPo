package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.WaybillAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.StaffModel
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WaybillControlActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showListDialog()
        }
    }
    private fun showListDialog() {
        val items = resources.getStringArray(R.array.waybillcontrol)
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
        tvTitle.text = "运单记录"
        tvTitleRight.text = "更多"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        val adapter = WaybillAdapter<WaybillModel>()
        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        adapter.itemListener = this

        HttpNetUtils.getInstance().getManager()?.wlget(
            hashMapOf(
                "bossId" to loginModel?.bossId!!,
                "index" to 0,
                "pagesize" to 200,
                "mobile" to loginModel?.mobile!!,
                "id" to loginModel?.id!!
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<WaybillModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<WaybillModel>>?) {
                    if (data?.data == null) {
                        toast(data?.msg!!)
                        return
                    }
                    adapter.setDataEntityList(data.data!!)
                }
            })
    }

}

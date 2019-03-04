package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.WaybillAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.iter.OnItemLongEventListener
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class WaybillActivity : BaseActivity(), OnItemEventListener, View.OnClickListener, OnItemLongEventListener {
    override fun onItemLongEvent(pos: Int) {
        showListDialog(pos)
    }

    override fun onItemEvent(pos: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivityForResult<SearchActivity>(119)
        }
    }


    private fun showListDialog(itemPos: Int) {
        val items = resources.getStringArray(R.array.waybillhistroy)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { dialog, which ->
            dialog.dismiss()
            when (which) {
                0 -> {
                }
            }
        }
        listDialog.show()
    }


    private val allItem: ArrayList<WaybillModel> by lazy { ArrayList<WaybillModel>() }
    private val adapter by lazy { WaybillAdapter<WaybillModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.text = "运单记录"
        tvTitleRight.setText(R.string.more)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)


        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        adapter.itemListener = this
        adapter.itemLongListener = this

        HttpNetUtils.getInstance().getManager()?.wlget(
            hashMapOf(
                "bossId" to loginModel?.bossId!!,
                "index" to 0,
                "pagesize" to 200,
                "mobile" to loginModel?.mobile!!,
                "id" to loginModel?.id!!,
                "sessionId" to loginModel?.sessionid!!
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<WaybillModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<WaybillModel>>?) {
                    if (data?.data == null) {
                        toast(data?.msg!!)
                        return
                    }
                    allItem.clear()
                    allItem.addAll(data.data!!)
                    adapter.setDataEntityList(allItem)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            119 -> {
                val orderid = data.getStringExtra("orderid")
                val receiverphone = data.getStringExtra("receiverphone")
                val recno = data.getIntExtra("recno", 0)
                val recpoint = data.getStringExtra("recpoint")
                val senderphone = data.getStringExtra("senderphone")
                val endDate = data.getStringExtra("endDate")

                HttpNetUtils.getInstance().getManager()?.wlsearch(
                    hashMapOf(
                        "orderid" to orderid,
                        "receiverphone" to receiverphone,
                        "recno" to recno,
                        "recpoint" to recpoint,
                        "senderphone" to senderphone,
                        "startDate" to endDate,
                        "id" to loginModel?.id!!,
                        "mobile" to loginModel?.mobile!!,
                        "sessionId" to loginModel?.sessionid!!
                    )
                )?.compose(NetworkScheduler.compose())
                    ?.subscribe(object : ProgressSubscriber<BaseModel<List<WaybillModel>>>(this) {
                        override fun onSuccess(data: BaseModel<List<WaybillModel>>?) {
                            if (data?.data == null) {
                                toast(data?.msg!!)
                                return
                            }
                            allItem.clear()
                            allItem.addAll(data.data!!)
                            adapter.setDataEntityList(allItem)
                        }
                    })
            }
        }
    }
}

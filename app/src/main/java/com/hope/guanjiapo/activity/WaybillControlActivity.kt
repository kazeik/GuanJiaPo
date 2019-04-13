package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.CompoundButton
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.WaybillAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_waybill.*
import kotlinx.android.synthetic.main.activity_waybill_controll.*
import kotlinx.android.synthetic.main.activity_waybill_controll.cbAll
import kotlinx.android.synthetic.main.activity_waybill_controll.rcvData
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

class WaybillControlActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            for (i in 0 until allItem.size)
                adapter.itemsStatus.put(i, true)
        } else {
            for (i in 0 until allItem.size)
                adapter.itemsStatus.put(i, false)
        }
        adapter.notifyDataSetChanged()
    }

    private fun showOrderStatusDialog() {
        val items = resources.getStringArray(R.array.orderstatus)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle(R.string.changeorderstatus)
        listDialog.setItems(items) { dialog, which ->
            dialog.dismiss()
            orderstatus = which
            changeState()
        }
        listDialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivityForResult<SearchActivity>(119)
            R.id.btnAllow -> {
                val temp = getChooice()
                if (temp.isNullOrEmpty()) {
                    toast("请选择您要结帐的数据")
                    return
                }

                var allowData = ""
                temp.forEach {
                    allowData += "${allItem[it].id},"
                }
                allow(allowData)
            }
            R.id.btnChangeStatus -> {
                val temp = getChooice()
                if (temp.isNullOrEmpty()) {
                    toast("请选择您要修改的数据")
                    return
                }
                showOrderStatusDialog()
            }
        }
    }

    private fun allow(data: String) {
        HttpNetUtils.getInstance().getManager()?.wlEditStates(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!, "idStrs" to data.substring(0, data.length - 1), "shipfeestate" to 1
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
                if (data.code == "success") {
                    getList()
                }
            }
        })
    }

    private fun getChooice(): ArrayList<Int> {
        val arrayInt: ArrayList<Int> = arrayListOf()
        for (entry in adapter.itemsStatus.entries) {
            if (entry.value)
                arrayInt.add(entry.key)
        }
        return arrayInt
    }

    private var orderstatus: Int? = 0

    override fun getLayoutView(): Int {
        return R.layout.activity_waybill_controll
    }


    private val allItem: LinkedList<WaybillModel> by lazy { LinkedList<WaybillModel>() }
    private val adapter by lazy { WaybillAdapter<WaybillModel>(this) }
    override fun initData() {
        tvTitle.setText(R.string.histroy)
        tvTitleRight.setText(R.string.more)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        btnChangeStatus.setOnClickListener(this)
        btnAllow.setOnClickListener(this)
        cbAll.visibility = View.VISIBLE
        cbAll.setOnCheckedChangeListener(this)

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.adapter = adapter

        getList()
    }

    private fun getList() {
        HttpNetUtils.getInstance().getManager()?.wlget(
            hashMapOf(
                "bossId" to loginModel?.bossId!!,
                "index" to 0,
                "pagesize" to 200,
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!
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
                    for (i in 0 until allItem.size)
                        adapter.itemsStatus.put(i, false)
                    adapter.setDataEntityList(allItem)
                }
            })
    }

    private fun changeState() {
        val temp = getChooice()
        var items = ""
        temp.forEach {
            items += "${allItem[it].id},"
        }
        items = items.substring(0, items.length - 1)
        HttpNetUtils.getInstance().getManager()?.wlEditStates(
            hashMapOf(
                "idStrs" to items, "newState" to orderstatus!!, "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                if (data?.code == "success") {
                    temp.forEach {
                        allItem[it].oderstate = orderstatus!!
                    }
                    adapter.setDataEntityList(allItem)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            119 -> {
                val dmap = data.getSerializableExtra("data") as HashMap<String, Any>
                var tempData =
                    "onlyDriver=0&clientCategory=4&clientVersion=1.0&mobile=${loginModel?.mobile!!}&sessionId=$sessionid&id=${loginModel?.id!!}"
                var temp = ""
                dmap.entries.forEach {
                    if (it.value != "")
                        temp = "&${it.key}=${it.value}"

                }
                tempData += temp
                val requestBody = RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), tempData
                )
                HttpNetUtils.getInstance().getManager()?.wlsearch(
                    requestBody
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

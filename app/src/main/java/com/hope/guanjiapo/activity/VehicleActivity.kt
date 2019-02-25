package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.VehicleAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.VehicleModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast


class VehicleActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
        val itemlist = allcar?.split(",")
        showInputDialog(itemlist?.get(pos)!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showInputDialog("")
        }
    }

    private fun showInputDialog(msg: String) {
        val editText = EditText(this)
        if (!TextUtils.isEmpty(msg))
            editText.setText(msg)
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入车次").setView(editText)
        inputDialog.setPositiveButton(
            "确定"
        ) { dialog, which ->
            dialog.dismiss()
            val car = editText.text.toString()
            if (TextUtils.isEmpty(car)) {
                return@setPositiveButton
            }
            val datalist = allcar?.split(",")
            if (datalist?.contains(car)!!) {
                toast("列表中已有$car")
                return@setPositiveButton
            }

            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "id" to loginModel?.id!!,
                    "mobile" to loginModel?.mobile!!,
                    "recCarNoList" to allcar!!,
                    "sessionId" to loginModel?.sessionid!!
                )
            )
                ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                    override fun onSuccess(data: BaseModel<String>?) {
                        allcar = "$allcar,$car"
                        val itemlist = allcar?.split(",")
                        adapter.setDataEntityList(itemlist!!)
                    }
                })
        }.show()
    }

    private var allcar: String? = null
    private val adapter: VehicleAdapter<String> by lazy { VehicleAdapter<String>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.text = "车次"
        tvTitleRight.text = "新建"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.adapter = adapter
        adapter.itemListener = this


        HttpNetUtils.getInstance().getManager()?.getCompanyInfo(
            hashMapOf(
                "id" to loginModel?.id!!, "sessionId" to loginModel?.sessionid!!
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<VehicleModel>>(this) {
                override fun onSuccess(data: BaseModel<VehicleModel>?) {
                    if (data?.data == null) {
                        toast(data?.msg!!)
                        return
                    }
                    allcar = data.data?.reccarnolist
                    val itemlist = allcar?.split(",")
                    adapter.setDataEntityList(itemlist!!)
                }
            })
    }
}

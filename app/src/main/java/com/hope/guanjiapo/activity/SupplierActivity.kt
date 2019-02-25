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
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.vehicleModel
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast

class SupplierActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
        val itemlist = vehicleModel?.servicenamelist?.split(",")
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
        inputDialog.setTitle("请输入供应商名字").setView(editText)
        inputDialog.setPositiveButton(
            R.string.sure
        ) { dialog, which ->
            dialog.dismiss()
            val car = editText.text.toString()
            if (TextUtils.isEmpty(car)) {
                return@setPositiveButton
            }
            val datalist = vehicleModel?.servicenamelist?.split(",")
            if (datalist?.contains(car)!!) {
                toast("列表中已有$car")
                return@setPositiveButton
            }

            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "id" to ApiUtils.loginModel?.id!!,
                    "mobile" to ApiUtils.loginModel?.mobile!!,
                    "servicenamelist" to vehicleModel?.servicenamelist!!,
                    "sessionId" to ApiUtils.loginModel?.sessionid!!
                )
            )
                ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                    override fun onSuccess(data: BaseModel<String>?) {
                        val itemlist = "${vehicleModel?.servicenamelist},$car".split(",")
                        adapter.setDataEntityList(itemlist)
                    }
                })
        }.show()
    }

    private val adapter: VehicleAdapter<String> by lazy { VehicleAdapter<String>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.setText(R.string.gys)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        adapter.itemListener = this

        adapter.setDataEntityList(vehicleModel?.servicenamelist?.split(",")!!)

    }
}

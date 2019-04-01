package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.VehicleAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.iter.OnItemLongEventListener
import com.hope.guanjiapo.model.VehicleModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_waybill.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast

class SupplierActivity : BaseActivity(), OnItemEventListener, View.OnClickListener, OnItemLongEventListener {
    override fun onItemLongEvent(pos: Int) {
        showInputDialog(allitem[pos])
    }

    override fun onItemEvent(pos: Int) {
        val intt = Intent()
        intt.putExtra("data", allitem.get(pos))
        setResult(197, intt)
        finish()
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
        inputDialog.setNegativeButton(R.string.cancel, null)
        inputDialog.setPositiveButton(
            R.string.sure
        ) { dialog, _ ->
            dialog.dismiss()
            val car = editText.text.toString()
            if (TextUtils.isEmpty(car)) {
                return@setPositiveButton
            }
            if (allitem.contains(car)) {
                toast("列表中已有$car")
                return@setPositiveButton
            }
            allitem.add(car)
            var tempAll = ""
            tempAll.forEach {
                tempAll += "$it,"
            }
            tempAll = tempAll.substring(0, tempAll.length - 1)
            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "id" to loginModel?.id!!,
                    "mobile" to loginModel?.mobile!!,
                    "servicenamelist" to tempAll,
                    "sessionId" to loginModel?.sessionid!!
                )
            )
                ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                    override fun onSuccess(data: BaseModel<String>?) {
                        adapter.setDataEntityList(allitem)
                    }
                })
        }.show()
    }

    private val adapter: VehicleAdapter<String> by lazy { VehicleAdapter<String>() }
    private val allitem: ArrayList<String> by lazy { arrayListOf<String>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.setText(R.string.gys)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        llview.visibility = if (intent.getBooleanExtra("a", false)) View.GONE else View.VISIBLE

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        adapter.itemListener = this
        adapter.itemLongListener = this
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))


        HttpNetUtils.getInstance().getManager()?.getCompanyInfo(
            hashMapOf(
                "id" to ApiUtils.loginModel?.id!!,
                "sessionId" to ApiUtils.loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<VehicleModel>>(this) {
                override fun onSuccess(data: BaseModel<VehicleModel>?) {
                    allitem.clear()
                    allitem.addAll(data?.data?.servicenamelist?.split(",")!!.filterNot { TextUtils.isEmpty(it) })
                    adapter.setDataEntityList(allitem)
                }
            })
    }
}

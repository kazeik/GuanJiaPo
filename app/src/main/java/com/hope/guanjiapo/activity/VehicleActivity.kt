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
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_waybill.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class VehicleActivity : BaseActivity(), OnItemEventListener, View.OnClickListener, OnItemLongEventListener {
    override fun onItemLongEvent(pos: Int) {
        showListDialog(pos)
    }

    override fun onItemEvent(pos: Int) {
        val intt = Intent()
        intt.putExtra("data", allcar[pos])
        setResult(201, intt)
        finish()
    }

    private fun showListDialog(pos: Int) {
        val items = resources.getStringArray(R.array.alertmenu)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> showInputDialog(allcar.get(pos), true)
                1 -> delete(pos)
            }
        }
        listDialog.show()
    }

    private fun delete(pos: Int) {
        allcar.removeAt(pos)

        var temp = ""
        allcar.forEach {
            temp += "$it,"
        }

        if (!TextUtils.isEmpty(temp))
            temp = temp.substring(0, temp.length - 1)
        edit(temp)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showInputDialog("", false)
        }
    }

    private fun showInputDialog(msg: String, edit: Boolean) {
        val editText = EditText(this)
        if (!TextUtils.isEmpty(msg))
            editText.setText(msg)
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入车次").setView(editText)
        inputDialog.setNegativeButton(R.string.cancel, null)
        inputDialog.setPositiveButton(
            R.string.sure
        ) { dialog, _ ->
            dialog.dismiss()
            val car = editText.text.toString()
            if (TextUtils.isEmpty(car)) {
                return@setPositiveButton
            }
            if (allcar.contains(car)) {
                toast("列表中已有$car")
                return@setPositiveButton
            }
            var tempAllString = ""
            if (edit) {
                val temp = allcar.filterNot { it == msg }
                allcar.clear()
                allcar.addAll(temp)
            }
            allcar.add(car)
            allcar.forEach {
                tempAllString += "$it,"
            }
            tempAllString = tempAllString.substring(0, tempAllString.length - 1)
            edit(tempAllString)
        }.show()
    }

    private fun edit(str: String) {
        val data =
            "clientCategory=4&clientVersion=1.0&id=${loginModel?.id}&isadd=1&mobile=${loginModel?.mobile}&sessionId=$sessionid&recCarNoList=$str"
        val requestBody = RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data
        )
        HttpNetUtils.getInstance().getManager()?.editCompanyInfo(requestBody)
            ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    adapter.setDataEntityList(allcar)
                }
            })
    }

    private val allcar: ArrayList<String> by lazy { ArrayList<String>() }
    private val adapter: VehicleAdapter<String> by lazy { VehicleAdapter<String>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.setText(R.string.carnum)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        llview.visibility = if (intent.getBooleanExtra("a", false)) View.GONE else View.VISIBLE

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.adapter = adapter
        adapter.itemListener = this
        adapter.itemLongListener = this

        HttpNetUtils.getInstance().getManager()?.getCompanyInfo(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<VehicleModel>>(this) {
                override fun onSuccess(data: BaseModel<VehicleModel>?) {
                    if (data?.data == null) {
                        toast(data?.msg!!)
                        return
                    }
                    val itemlist = data.data?.reccarnolist?.split(",")?.filterNot { TextUtils.isEmpty(it) }
                    allcar.addAll(itemlist!!)
                    adapter.setDataEntityList(allcar)
                }
            })
    }
}

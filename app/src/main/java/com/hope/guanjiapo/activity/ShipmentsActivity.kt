package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.ShipmentsAdapter
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
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast


class ShipmentsActivity : BaseActivity(), View.OnClickListener, OnItemEventListener, OnItemLongEventListener {
    override fun onItemLongEvent(pos: Int) {
        showListDialog(pos)
    }

    override fun onItemEvent(pos: Int) {
        val intt = Intent()
        intt.putExtra("data", allitem.get(pos))
        setResult(195, intt)
        finish()
    }

    private fun showListDialog(pos: Int) {
        val items = resources.getStringArray(R.array.alertmenu)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> {
                    val msg = allitem[pos]
                    allitem.removeAt(pos)
                    showInputDialog(msg)
                }
                1 -> delete(pos)
            }
        }
        listDialog.show()
    }

    private fun showInputDialog(msg: String) {
        val editText = EditText(this)
        if (!TextUtils.isEmpty(msg))
            editText.setText(msg)
        val inputDialog = AlertDialog.Builder(this)
        inputDialog.setTitle("请输入发货点").setView(editText)
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
            var tempAllString = ""
            allitem.add(car)
            allitem.forEach {
                tempAllString += "$it,"
            }
            tempAllString = tempAllString.substring(0, tempAllString.length - 1)
            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "id" to loginModel?.id!!,
                    "clientCategory" to 4,
                    "clientVersion" to 1.0,
                    "mobile" to loginModel?.mobile!!,
                    "sessionId" to sessionid!!,
                    "faHuoDiList" to tempAllString
                )
            )
                ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                    override fun onSuccess(data: BaseModel<String>?) {
                        adapter.setDataEntityList(allitem)
                    }
                })
        }.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showInputDialog("")
        }
    }

    private val adapter: ShipmentsAdapter<String> by lazy { ShipmentsAdapter<String>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_search_recycler
    }

    private val allitem: ArrayList<String> by lazy { arrayListOf<String>() }
    override fun initData() {
        tvTitle.setText(R.string.fhdlist)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter
        adapter.itemLongListener = this
        adapter.itemListener = this

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val msg = etSearch.text.toString()
                val templist = allitem.filter { it.contains(msg) }
                adapter.setDataEntityList(templist)
            }
        })

        HttpNetUtils.getInstance().getManager()?.getCompanyInfo(
            hashMapOf("id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!,"type" to 1)
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<VehicleModel>>(this) {
                override fun onSuccess(data: BaseModel<VehicleModel>?) {
                    allitem.clear()
                    val allpoint = data?.data?.fahuodilist?.split(",")
                    allpoint?.forEach {
                        if (!TextUtils.isEmpty(it))
                            allitem.add(it)
                    }
                    adapter.setDataEntityList(allitem)
                }
            })
    }

    private fun delete(pos: Int) {
        allitem.removeAt(pos)
        var tempAllString = ""
        allitem.forEach {
            tempAllString += "$it,"
        }
        tempAllString.substring(0, tempAllString.length - 1)
        HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!,
                "faHuoDiList" to tempAllString
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                adapter.setDataEntityList(allitem)
                toast(data?.msg!!)
            }
        })
    }
}

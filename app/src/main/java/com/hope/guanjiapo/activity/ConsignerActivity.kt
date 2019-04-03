package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.ConsigneeAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.iter.OnItemLongEventListener
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ConsignerActivity : BaseActivity(), View.OnClickListener, OnItemEventListener, OnItemLongEventListener {
    override fun onItemLongEvent(pos: Int) {
        showListDialog(pos)
    }

    override fun onItemEvent(pos: Int) {
        val intt = Intent()
        intt.putExtra("data", allitem.get(pos))
        setResult(196, intt)
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivity<AddConsigneeActivity>()
        }
    }

    private fun showListDialog(pos: Int) {
        val items = resources.getStringArray(R.array.alertmenu)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> startActivity<AddConsigneeActivity>("type" to true, "data" to allitem[pos])
                1 -> delete(allitem[pos])
            }
        }
        listDialog.show()
    }

    private fun delete(model: ConsigneeModel) {
        HttpNetUtils.getInstance().getManager()?.connectordelete(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!,
                "bossid" to loginModel?.bossId!!,
                "customerid" to model.id
            )
        )
            ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                }
            })
    }

    private val adapter: ConsigneeAdapter<ConsigneeModel> by lazy { ConsigneeAdapter<ConsigneeModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_search_recycler
    }

    private val allitem: ArrayList<ConsigneeModel> by lazy { arrayListOf<ConsigneeModel>() }
    override fun initData() {
        tvTitle.setText(R.string.fhrlist)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter
        adapter.itemListener = this
        adapter.itemLongListener = this

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val msg = etSearch.text.toString()
                val templist =
                    allitem.filter { it.addr.contains(msg) || it.mobile.contains(msg) || it.name.contains(msg) }
                adapter.setDataEntityList(templist)
            }
        })

        HttpNetUtils.getInstance().getManager()?.getConnector(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!, "type" to 1
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<ConsigneeModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<ConsigneeModel>>?) {
                    allitem.clear()
                    allitem.addAll(data?.data!!)
                    adapter.setDataEntityList(data.data!!)
                }
            })
    }

}

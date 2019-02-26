package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.DestinationAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class DestinationActivity : BaseActivity(), View.OnClickListener, OnItemEventListener {
    override fun onItemEvent(pos: Int) {
        showListDialog(pos)
    }

    private fun showListDialog(pos: Int) {
        val items = resources.getStringArray(R.array.alertmenu)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> startActivity<AddDestinationActivity>("change" to true, "data" to allitem[pos])
                1 -> delete()
            }
        }
        listDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivity<AddDestinationActivity>()
        }
    }

    private val adapter: DestinationAdapter<DestinationModel> by lazy { DestinationAdapter<DestinationModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_search_recycler
    }

    private val allitem: ArrayList<DestinationModel> by lazy { arrayListOf<DestinationModel>() }
    override fun initData() {
        tvTitle.setText(R.string.mddlist)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter
        adapter.itemListener = this

        HttpNetUtils.getInstance().getManager()?.getcompanyPointList(
            hashMapOf("id" to loginModel?.id!!, "sessionId" to loginModel?.sessionid!!, "type" to 0)
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<DestinationModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<DestinationModel>>?) {
                    allitem.clear()
                    allitem.addAll(data?.data!!)
                    adapter.setDataEntityList(data.data!!)
                }
            })
    }

    private fun delete() {
        HttpNetUtils.getInstance().getManager()?.deletecompanyPoint(
            hashMapOf(
                "bossid" to loginModel?.bossId!!,
                "customerid" to 7,
                "id" to loginModel?.id!!,
                "sessionId" to loginModel?.sessionid!!,
                "mobile" to loginModel?.mobile!!
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
            }
        })
    }
}

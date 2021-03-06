package com.hope.guanjiapo.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.SubscribeAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.SubscribeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class SubscribeActivity : BaseActivity(), View.OnClickListener, OnItemEventListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_subscribe
    }

    private val allitem: ArrayList<SubscribeModel> by lazy { arrayListOf<SubscribeModel>() }
    private val adapter: SubscribeAdapter<SubscribeModel> by lazy { SubscribeAdapter<SubscribeModel>(this) }

    override fun initData() {
        tvTitle.setText(R.string.subscribe)
        tvTitleRight.setText(R.string.search)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter
        adapter.itemListener = this

        searchOrder()
    }

    private fun searchOrder() {
        HttpNetUtils.getInstance().getManager()?.wxsearch(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!,
                "onlyDriver" to 0,
                "keyword" to ""
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<SubscribeModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<SubscribeModel>>?) {
                    if (data?.code == "success") {
                        allitem.clear()
                        allitem.addAll(data.data!!)
                        adapter.setDataEntityList(allitem)
                    }
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivityForResult<OrderSearchActivity>(119)
        }
    }

    override fun onItemEvent(pos: Int) {
        startActivity<EditSubscribeActivity>("data" to allitem[pos])
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            119 -> {
                val orderid = data.getStringExtra("orderid")
                val receiverphone = data.getStringExtra("receiverphone")
                val senderphone = data.getStringExtra("senderphone")
                val startDate = data.getStringExtra("startDate")

                val map = hashMapOf(
                    "id" to loginModel?.id!!,
                    "clientCategory" to 4,
                    "clientVersion" to 1.0,
                    "mobile" to loginModel?.mobile!!,
                    "sessionId" to sessionid!!,
                    "onlyDriver" to 0
                )
                if (!TextUtils.isEmpty(orderid))
                    map["orderid"] = orderid
                if (!TextUtils.isEmpty(receiverphone))
                    map["receiverphone"] = receiverphone
                if (!TextUtils.isEmpty(senderphone))
                    map["senderphone"] = senderphone
                if (!TextUtils.isEmpty(startDate))
                    map["startDate"] = startDate
                HttpNetUtils.getInstance().getManager()?.wxsearch(map)
                    ?.compose(NetworkScheduler.compose())
                    ?.subscribe(object : ProgressSubscriber<BaseModel<List<SubscribeModel>>>(this) {
                        override fun onSuccess(data: BaseModel<List<SubscribeModel>>?) {
                            if (data?.code == "success") {
                                allitem.clear()
                                allitem.addAll(data.data!!)
                                adapter.setDataEntityList(allitem)
                            }
                        }
                    })
            }
        }
    }
}

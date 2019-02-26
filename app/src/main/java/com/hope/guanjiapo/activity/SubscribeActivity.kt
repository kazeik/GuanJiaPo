package com.hope.guanjiapo.activity

import android.support.v7.widget.LinearLayoutManager
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
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class SubscribeActivity : BaseActivity(), View.OnClickListener, OnItemEventListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_subscribe
    }

    private val adapter: SubscribeAdapter<SubscribeModel> by lazy { SubscribeAdapter<SubscribeModel>() }
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
                "onlyDriver" to 0,
                "keyword" to "",
                "id" to ApiUtils.loginModel?.id!!,
                "sessionId" to ApiUtils.loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<SubscribeModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<SubscribeModel>>?) {
                    adapter.setDataEntityList(data?.data!!)
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivity<AddConsigneeActivity>()
        }
    }

    override fun onItemEvent(pos: Int) {
    }

}

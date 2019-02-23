package com.hope.guanjiapo.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.ConsigneeAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class ConsigneeActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivity<AddConsigneeActivity>()
        }
    }

    private val adapter: ConsigneeAdapter<ConsigneeModel> by lazy { ConsigneeAdapter<ConsigneeModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_search_recycler
    }

    override fun initData() {
        tvTitle.text = "收货人列表"
        tvTitleRight.text = "新建"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter

        HttpNetUtils.getInstance().getManager()?.getConnector(
            hashMapOf("id" to loginModel?.id!!, "sessionId" to loginModel?.sessionid!!, "type" to 0)
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<ConsigneeModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<ConsigneeModel>>?) {
                    adapter.setDataEntityList(data?.data!!)
                }
            })
    }

}

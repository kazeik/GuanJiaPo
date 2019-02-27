package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
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
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class ConsigneeActivity : BaseActivity(), View.OnClickListener, OnItemEventListener ,OnItemLongEventListener{
    override fun onItemLongEvent(pos: Int) {
        showListDialog(pos)
    }

    override fun onItemEvent(pos: Int) {
        val intt = Intent()
        intt.putExtra("data", allitem.get(pos))
        setResult(198, intt)
        finish()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivity<AddConsigneeActivity>()
        }
    }

    private fun delete(model: ConsigneeModel) {

    }

    private val adapter: ConsigneeAdapter<ConsigneeModel> by lazy { ConsigneeAdapter<ConsigneeModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_search_recycler
    }

    private val allitem: ArrayList<ConsigneeModel> by lazy { arrayListOf<ConsigneeModel>() }
    override fun initData() {
        tvTitle.setText(R.string.shrlist)
        tvTitleRight.setText(R.string.create)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvDataList.layoutManager = LinearLayoutManager(this)
        rcvDataList.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvDataList.adapter = adapter
        adapter.itemListener=this
        adapter.itemLongListener= this

        HttpNetUtils.getInstance().getManager()?.getConnector(
            hashMapOf("id" to loginModel?.id!!, "sessionId" to loginModel?.sessionid!!, "type" to 0)
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

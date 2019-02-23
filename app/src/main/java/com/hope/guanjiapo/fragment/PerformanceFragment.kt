package com.hope.guanjiapo.fragment


import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PerformanceAdapter
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.base.LazyFragment
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.PerformanceListModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*


/**
 * A simple [Fragment] subclass.
 *
 */
class PerformanceFragment : LazyFragment(), OnItemEventListener, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvTitleRight -> showListDialog()
        }
    }

    override fun lazyLoad() {
        getData()
    }

    override fun onItemEvent(pos: Int) {

    }

    override fun initView(): Int {
        return com.hope.guanjiapo.R.layout.fragment_data
    }

    private fun showListDialog() {
        val items = resources.getStringArray(R.array.listdialog)
        val listDialog = AlertDialog.Builder(activity)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> getData()
                1 -> getDataByDay()
            }
        }
        listDialog.show()
    }

    private val adapter: PerformanceAdapter<PerformanceListModel> by lazy { PerformanceAdapter<PerformanceListModel>() }
    override fun bindData() {
        tvTitle.text = "业绩"
        ivBackup.visibility = View.GONE
        tvTitleRight.visibility = View.VISIBLE
        tvTitleRight.text = "更多"
        tvTitleRight.setOnClickListener(this)

        rcvData.layoutManager = LinearLayoutManager(activity)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(activity!!, RecyclerView.VERTICAL))
        adapter.itemListener = this

    }

    private fun getData() {
        HttpNetUtils.getInstance().getManager()?.getmonthrevenue(
            hashMapOf(
                "clientCategory" to 3,
                "clientVersion" to "1.0",
                "id" to loginModel?.id!!,
                "isBuy" to 0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<PerformanceListModel>>>(activity!!) {
                override fun onSuccess(data: BaseModel<List<PerformanceListModel>>?) {
                    adapter.setDataEntityList(data?.data!!)
                }
            })
    }

    private fun getDataByDay() {
        HttpNetUtils.getInstance().getManager()?.getmonthrevenue(
            hashMapOf(
                "clientCategory" to 3,
                "clientVersion" to "1.0",
                "id" to loginModel?.id!!,
                "isBuy" to 0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<PerformanceListModel>>>(activity!!) {
                override fun onSuccess(data: BaseModel<List<PerformanceListModel>>?) {
                    adapter.setDataEntityList(data?.data!!)
                }
            })
    }
}

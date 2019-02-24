package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.StaffAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.StaffModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_staff.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class StaffActivity : BaseActivity(), View.OnClickListener, OnItemEventListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight ->
                startActivityForResult(Intent(this, AddStaffActivity::class.java).putExtra("change", false), 99)
        }
    }

    override fun onItemEvent(pos: Int) {
        showListDialog(pos)
    }

    private fun showListDialog(pos: Int) {
        val items = arrayOf("修改", "删除")
        val listDialog = AlertDialog.Builder(this)
        listDialog.setItems(items) { _, which ->
            when (which) {
                0 -> startActivity<AddStaffActivity>("change" to true, "item" to itemData!![pos])
                1 -> delete(itemData!![pos])
            }
        }
        listDialog.show()
    }

    private var itemData: ArrayList<StaffModel>? = null
    private val adapter: StaffAdapter<StaffModel> by lazy { StaffAdapter<StaffModel>() }
    override fun getLayoutView(): Int {
        return R.layout.activity_staff
    }

    private fun delete(item: StaffModel) {
        HttpNetUtils.getInstance().getManager()?.wladdOrDel(
            hashMapOf(
                "isAdd" to 0,
                "memberMobile" to item.mobile,
                "id" to ApiUtils.loginModel?.id!!,
                "mobile" to item.mobile,
                "sessionId" to ApiUtils.loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<ArrayList<StaffModel>>>(this) {
                override fun onSuccess(data: BaseModel<ArrayList<StaffModel>>?) {
                    itemData = data?.data
                }
            })
    }

    override fun initData() {
        tvTitle.text = "员工管理"
        tvTitleRight.text = "添加"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (resultCode) {
            99 -> {
                itemData = intent.getSerializableExtra("data") as ArrayList<StaffModel>
                adapter.setDataEntityList(itemData!!)
            }
        }
    }
}
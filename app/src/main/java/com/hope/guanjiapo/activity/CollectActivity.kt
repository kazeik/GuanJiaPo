package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.CollectAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.TimeUtil
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.adapter_setting.*
import kotlinx.android.synthetic.main.view_dialog_loading.*
import kotlinx.android.synthetic.main.view_title.*
import android.widget.Toast
import android.content.DialogInterface
import org.jetbrains.anko.startActivity


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 03 12 10:17
 * 类说明:
 */
class CollectActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_collect
    }

    private val adapter: CollectAdapter<WaybillModel> by lazy { CollectAdapter<WaybillModel>() }
    @SuppressLint("SetTextI18n")
    override fun initData() {
        tvTitle.text = "汇总交接单"
        ivBackup.setOnClickListener(this)
        tvTitleRight.visibility = View.VISIBLE
        tvTitleRight.setText(R.string.more)
        tvTitleRight.setOnClickListener(this)

        val allData = intent.getSerializableExtra("data") as ArrayList<WaybillModel>

        tvPrintTime.text = "打印时间:${TimeUtil.getDayByType(
            System.currentTimeMillis(),
            TimeUtil.DATE_YMS
        )}  操作员:${loginModel?.mobile} 总行数:${allData.size}"

        tvInfo.text = PreferencesUtils.getString(this, "footer")
        var allNum = 0.0
        var allMoney = 0.0
        allData.forEach {
            allMoney += it.baseshipfee
            allNum += it.productcount
        }

        tvAllNum.text = "$allNum"
        tvAllMoney.text = "$allMoney"
        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.isNestedScrollingEnabled = false
        rcvData.adapter = adapter
        adapter.setDataEntityList(allData)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showListDialog()
        }
    }

    private fun showListDialog() {
        val items = arrayOf("打印设置", "打印小票")
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择")
        listDialog.setItems(items) { dialog, which ->
            when (which) {
                0 -> startActivity<ConfigPrintActivity>()
                1 -> {
                }
            }
        }
        listDialog.show()
    }
}
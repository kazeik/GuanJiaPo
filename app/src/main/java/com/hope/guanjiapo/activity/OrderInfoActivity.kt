package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.vehicleModel
import kotlinx.android.synthetic.main.activity_order_info.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast


class OrderInfoActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_order_info
    }

    private var fhdStr: String? = ""
    private var ccStr: String? = ""
    private var bzdwStr: String? = ""

    override fun initData() {
        tvTitle.setText(R.string.orderinfo)
        tvTitleRight.setText(R.string.save)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvFwhy.setOnClickListener(this)
        tvCc.setOnClickListener(this)
        tvBzdw.setOnClickListener(this)
        tvFhd.setOnClickListener(this)
        ivMdd.setOnClickListener(this)
        ivShr.setOnClickListener(this)
        ivGys.setOnClickListener(this)
        ivLxr.setOnClickListener(this)
    }

    private fun showCcListDialog() {
        val items = vehicleModel?.reccarnolist?.split(",")?.toTypedArray()
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择车次")
        listDialog.setItems(items) { dialog, which ->
            ccStr = items?.get(which)
            tvCc.text = ccStr
        }
        listDialog.show()
    }

    private fun showBzdwListDialog() {
        val items = resources.getStringArray(R.array.bzdw)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装单位")
        listDialog.setItems(items, { dialog, which ->
        })
        listDialog.show()
    }

    private fun showFhdListDialog() {
        val items = arrayOf("我是1", "我是2", "我是3", "我是4")
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择发货点")
        listDialog.setItems(items, { dialog, which ->
        })
        listDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> createOrder()
            R.id.tvFwhy -> startActivity<PremiumActivity>()
            R.id.tvCc -> showCcListDialog()
            R.id.tvBzdw -> showBzdwListDialog()
            R.id.tvFhd -> showFhdListDialog()
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShr -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivGys -> startActivityForResult<SupplierActivity>(197)
            R.id.ivLxr -> startActivityForResult<ConsignerActivity>(196)
        }
    }


    private fun createOrder() {
        val kddhStr = etKddh.text.toString()
        val mddStr = etMdd.text.toString()
        val shrStr = etShr.text.toString()
        val hwmcStr = etHwmc.text.toString()
        val hwslStr = etHwsl.text.toString()
        val zlStr = etZl.text.toString()
        val tjStr = etTj.text.toString()
        val jbyfStr = etJbyf.text.toString()
        val psfStr = etPsh.text.toString()
        val bfStr = etBf.text.toString()
        val zzfStr = etZzh.text.toString()
        val dskStr = etDsk.text.toString()
        val cbStr = etCb.text.toString()
        val gysStr = etGys.text.toString()
        val fhrStr = etFhr.text.toString()
        val bzStr = etBz.text.toString()

        HttpNetUtils.getInstance().getManager()?.wladd(hashMapOf())?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            199 -> {
                val item = data.getSerializableExtra("data") as DestinationModel
                etMdd.setText(item.receivepoint)
            }
            198 -> {
                val item = data.getSerializableExtra("data") as ConsigneeModel
                etShr.setText(item.name)
            }
            197 -> {
                val itemStr = data.getStringExtra("data")
                etGys.setText(itemStr)
            }
            196 -> {
                val item = data.getSerializableExtra("data") as ConsigneeModel
                etFhr.setText(item.name)
            }
        }
    }
}

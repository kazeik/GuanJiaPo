package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.model.SubscribeModel
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.utils.Utils.logs
import kotlinx.android.synthetic.main.activity_edit_subscribe.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.json.JSONObject


class EditSubscribeActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_edit_subscribe
    }

    private var fkStr: String = ""
    private var hdfsStr: String = ""
    private var tzfh: Int = 0
    private var subscribeModel: SubscribeModel? = null
    private var orderStatus: Int? = 0
    private var fhrModel: ConsigneeModel? = null //发货人
    private var shrModel: ConsigneeModel? = null //发货人
    private var bzdw: Int? = 0
    private var cc: Int? = 0
    private var yf: Double? = 0.0
    private var shipfeepaytype: Int = 0
    private var recway: Int = 0

    private var payType: Int = 0

    override fun initData() {
        subscribeModel = intent.getSerializableExtra("data") as SubscribeModel

        tvTitle.setText(R.string.subscribe)
        tvTitleRight.setText(R.string.tozs)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvFwhy.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        ivMdd.setOnClickListener(this)
        ivShr.setOnClickListener(this)
        tvYdzt.setOnClickListener(this)
        ivFhr.setOnClickListener(this)
        ivFhd.setOnClickListener(this)
        tvBzdw.setOnClickListener(this)
        tvCc.setOnClickListener(this)

        mPayTypeGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbXf -> shipfeepaytype = 0
                R.id.rbYj -> shipfeepaytype = 1
                R.id.rbTf -> shipfeepaytype = 2
            }
        }
        msgPayYf.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            when (i) {
                R.id.rbQk -> payType = 0
                R.id.rbYf -> payType = 1
            }
        }
        msgShfs.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            when (i) {
                R.id.rbZt -> recway = 0
                R.id.rbPs -> recway = 1
            }
        }

        etFhr.setText(subscribeModel?.sendername)
        etFhdh.setText(subscribeModel?.senderphone)
        etFhdz.setText(subscribeModel?.senderaddress1)
        etBz.setText(subscribeModel?.comment)
        etDh.setText(subscribeModel?.id)
        etYwy.setText(subscribeModel?.operatorMobile)
        etShr.setText(subscribeModel?.receivername)
        etShdh.setText(subscribeModel?.receiverphone)
        etShdz.setText(subscribeModel?.receiveraddress)
        etHwmc.setText(subscribeModel?.productdescript)
        etHwsl.setText(subscribeModel?.productcount)
        etZl.setText(subscribeModel?.productweight)
        etTj.setText(subscribeModel?.productsize)
        etZzz.setText(subscribeModel?.transferName)
        etZzh.setText(subscribeModel?.shipfeesendpay)
        etBf.setText(subscribeModel?.insurancefee)
        etDsk.setText(
            subscribeModel?.agentmoney
        )

        when (subscribeModel?.shipfeepaytype) {
            0 -> rbXf.isChecked = true
            1 -> rbYj.isChecked = true
            2 -> rbTf.isChecked = true
        }
        when (subscribeModel?.shipfeestate) {
            0 -> rbQk.isChecked = true
            1 -> rbYf.isChecked = true
        }
        when (subscribeModel?.recway) {
            0 -> rbZt.isChecked = true
            1 -> rbPs.isChecked = true
        }

        val stat = resources.getStringArray(R.array.orderstatus)
        tvYdzt.text = stat[subscribeModel?.status!!]

        etJbyf.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etPsh.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etBf.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etZzh.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etDsk.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun check() {
        val item1 = etJbyf.text.toString()
        val item2 = etPsh.text.toString()
        val item3 = etBf.text.toString()
        val item4 = etZzh.text.toString()
        val item5 = etDsk.text.toString()
        val p0: Double? = if (TextUtils.isEmpty(item1)) 0.0 else item1.toDouble()
        val p1: Double? = if (TextUtils.isEmpty(item2)) 0.0 else item2.toDouble()
        val p2: Double? = if (TextUtils.isEmpty(item3)) 0.0 else item3.toDouble()
        val p3: Double? = if (TextUtils.isEmpty(item4)) 0.0 else item4.toDouble()
        val p4: Double? = if (TextUtils.isEmpty(item5)) 0.0 else item5.toDouble()
        yf = p0!! + p1!! + p2!!
        val hj = p0 + p1 + p2 + p3!! + p4!!
        tvAllMoney.text = "应收运费：$yf 合计(含代收中转):$hj"
    }

    private fun showCcListDialog() {
        val items = ApiUtils.vehicleModel?.reccarnolist?.split(",")?.toTypedArray()
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择车次")
        listDialog.setItems(items) { dialog, which ->
            cc = which
            tvCc.text = items?.get(which)
        }
        listDialog.show()
    }


    private fun showBzdwListDialog() {
        val items = resources.getStringArray(R.array.bzdw)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装单位")
        listDialog.setItems(items) { dialog, which ->
            bzdw = which
            tvBzdw.text = items[which]
        }
        listDialog.show()
    }

    private fun showOrderStatusDialog() {
        val items = resources.getStringArray(R.array.orderstatus)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装")
        listDialog.setItems(items) { dialog, which ->
            orderStatus = which
            tvYdzt.text = items[which]
        }
        listDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> createOrder()
            R.id.tvFwhy -> startActivityForResult<PremiumActivity>(
                200,
                "fk" to fkStr,
                "hdfs" to hdfsStr,
                "tzfh" to tzfh
            )
            R.id.btnDelete -> delete()
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShr -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivFhr -> startActivityForResult<ConsignerActivity>(196)
            R.id.tvYdzt -> showOrderStatusDialog()
            R.id.tvBzdw -> showBzdwListDialog()
            R.id.tvCc -> showCcListDialog()
            R.id.ivFhd -> startActivityForResult<ShipmentsActivity>(195)
        }
    }

    private fun createOrder() {
        val fhdStr = etFhd.text.toString()
        val fhrStr = etFhr.text.toString()
        val fhdhStr = etFhdh.text.toString()
        val fhdzStr = etFhdz.text.toString()
        val bz = etBz.text.toString()
        val dhStr = etDh.text.toString()
        val ywyStr = etYwy.text.toString()
        val mddStr = etMdd.text.toString()
        val shrStr = etShr.text.toString()
        val shdhStr = etShdh.text.toString()
        val shdzStr = etShdz.text.toString()
        val hwmcStr = etHwmc.text.toString()
        val hwslStr = etHwsl.text.toString()
        val zlStr = etZl.text.toString()
        val tjStr = etTj.text.toString()
        val jbyhStr = etJbyf.text.toString()
        val psfStr = etPsh.text.toString()
        val zzzStr = etZzz.text.toString()
        val zzhStr = etZzh.text.toString()
        val bfStr = etBf.text.toString()
        val dskStr = etDsk.text.toString()

        val order = JSONObject(
            hashMapOf(
                "transferName" to zzzStr,
                "agentmoney" to dskStr,
                "copycount" to hdfsStr, //回单份数
                "shipfeesendpay" to zzhStr, //中转费
                "shipfee" to yf, //运费 （总计
                "serviceName" to "",//供应商
                "dispatchfee" to psfStr, //派送费
                "receivername" to shrStr,//收货人
                "productweight" to zlStr,//重量
                "receivepoint" to mddStr, //目的地
                "shipfeesendpay" to zzhStr,//中转费
                "costFee" to "", //成本
                "senderphone" to fhrModel?.mobile,//发货人电话
                "shipfeepaytype" to shipfeepaytype, //支付方式
                "sendername" to fhrStr,//发货人
                "receiveraddress" to shrModel?.addr, //收货人的地址
                "waitnotify" to tzfh,//等通知放货，0否，1是
                "productno" to dhStr,//快递单号
                "baseshipfee" to jbyhStr, //基本运费
                "insurancefee" to bfStr, //保费
                "recno" to bzdw,
                "productcount" to hwslStr,//货物数量
                "recway" to recway,//收货方式，0自提，1派送
                "productsize" to tjStr,//体积
                "receiverphone" to shrModel?.mobile,//收货人电话
                "productdescript" to hwmcStr, //货物名称
                "returnmoney" to fkStr,//返款
                "carname" to cc!!,//车次
                "comment" to bz, //备注
                "senderaddress" to fhrModel?.addr,//发货人地址
                "shipFeeState" to if (shipfeepaytype == 0) "1" else "0" //运费支付，0欠款，1已付
            )
        ).toString()
        val data =
            "clientCategory=4&clientVersion=1.0&id=${loginModel?.id}&isadd=1&mobile=${loginModel?.mobile}&sessionId=$sessionid&order=$order"
        val requestBody = RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data
        )
        HttpNetUtils.getInstance().getManager()?.wladd(
            requestBody
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<WaybillModel>>(this) {
                override fun onSuccess(data: BaseModel<WaybillModel>?) {
                    if (data?.code == "success")
                        delete()
                    else
                        toast(data?.msg!!)
                }
            })
    }


    private fun delete() {
        HttpNetUtils.getInstance().getManager()?.wxdelete(
            hashMapOf(
                "isDel" to 1,
                "orderid" to subscribeModel?.id!!,
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!
            )
        )
            ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                    if (data.code == "success") {
                        val waybill = subscribeModel as WaybillModel
                        waybill.oderstate = subscribeModel?.status

                        logs("tag", "waybill = $waybill | subscri = $subscribeModel")
                        startActivity<PrintOrderInfoActivity>("data" to waybill)
                        finish()
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            200 -> {
                fkStr = data.getStringExtra("fk")
                hdfsStr = data.getStringExtra("hdfs")
                tzfh = data.getIntExtra("tzfh", 0)
            }
            199 -> {
                val item = data.getSerializableExtra("data") as DestinationModel
                etMdd.setText(item.receivepoint)
            }
            198 -> {
                shrModel = data.getSerializableExtra("data") as ConsigneeModel
                etShr.setText(shrModel?.name)
                etShdh.setText(shrModel?.mobile)
                etShdz.setText(shrModel?.addr)
            }
            196 -> {
                fhrModel = data.getSerializableExtra("data") as ConsigneeModel
                etFhr.setText(fhrModel?.name)
                etFhdh.setText(fhrModel?.mobile)
                etFhdz.setText(fhrModel?.addr)
            }
            195 -> {
//                val destinationModel = data.getSerializableExtra("data") as DestinationModel
                etFhd.setText(data.getStringExtra("data"))
            }
        }
    }
}

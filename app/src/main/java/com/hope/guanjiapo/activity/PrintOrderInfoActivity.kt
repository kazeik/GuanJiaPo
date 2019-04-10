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
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.utils.ApiUtils.staffModel
import kotlinx.android.synthetic.main.activity_print_order_info.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.json.JSONObject


class PrintOrderInfoActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_print_order_info
    }

    private val items by lazy { resources.getStringArray(R.array.bzdw) }
    private var ccStr: String? = ""
    private var bzdwStr: Int? = 0
    private var recway: Int = 0
    private var shipfeepaytype: Int = 0
    private var fkStr: String = ""
    private var hdfsStr: String = ""
    private var tzfh: Int = 0

    private var fhrModel: ConsigneeModel? = null //发货人
    private var shrModel: ConsigneeModel? = null //货人

    private var waybillModel: WaybillModel? = null


    @SuppressLint("SetTextI18n")
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
        ivFhd.setOnClickListener(this)

        btnPrintXp.setOnClickListener(this)
        btnPrintBq.setOnClickListener(this)
        btnDelete.setOnClickListener(this)

        waybillModel = intent.getSerializableExtra("data") as WaybillModel

        etJbyf.setText(waybillModel?.baseshipfee)
        etOrderId.setText(waybillModel?.id!!)
        etMdd.setText(waybillModel?.receivepoint)
        etShr.setText(waybillModel?.receivername)
        etPsh.setText(waybillModel?.dispatchfee)
        etZzh.setText(waybillModel?.shipfeesendpay)
        etGys.setText(waybillModel?.serviceName)
        etFhr.setText(waybillModel?.sendername)
        etTj.setText(waybillModel?.productsize)
        etHwsl.setText(waybillModel?.productcount)
        etCb.setText(waybillModel?.costFee)
        etBz.setText(waybillModel?.comment)
        etKddh.setText(waybillModel?.productno)
        etHwmc.setText(waybillModel?.productdescript)
        etZl.setText(waybillModel?.productweight)
        etDsk.setText(waybillModel?.agentmoney)
        etFhd.setText(waybillModel?.senderaddress)
        etBf.setText(waybillModel?.insurancefee)
        tvCc.text = waybillModel?.carname
//        etFk.setText(waybillModel?.returnmoney  )
//        etHdfs.setText(waybillModel?.copycount  )

        val yf =
            getValue(waybillModel?.baseshipfee) + getValue(waybillModel?.dispatchfee) + getValue(waybillModel?.insurancefee)
        val al = yf + getValue(waybillModel?.agentmoney) + getValue(waybillModel?.shipfeesendpay)
        tvAllMoney.text =
            "运费：$yf 合计(含代收中转):$al"

        if (staffModel != null && staffModel?.isNotEmpty()!!) {
            val tempStaff = staffModel?.singleOrNull { it.mobile == waybillModel?.operatorMobile }
            etYwy.setText(tempStaff?.userName)
        }

        tvOrderStatus.text = when (waybillModel?.oderstate) {
            0 -> "待装车"
            1 -> "运输中"
            2 -> "已到站"
            3 -> "已提货"
            4 -> "已收款"
            5 -> "已打款"
            6 -> "已开票"
            else -> ""
        }

        tvBzdw.text = items[waybillModel?.recno!!]

        mPayTypeGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbXf -> shipfeepaytype = 0
                R.id.rbYj -> shipfeepaytype = 1
                R.id.rbTf -> shipfeepaytype = 2
            }
        }
        msgShfs.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbZt -> recway = 0
                R.id.rbPs -> recway = 1
            }
        }

        if (waybillModel?.recway == 0) {
            rbZt.isChecked = true
        } else
            rbPs.isChecked = true

        when (waybillModel?.shipfeepaytype) {
            0 -> rbXf.isChecked = true
            1 -> rbYj.isChecked = true
            2 -> rbTf.isChecked = true
        }

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

    private fun getValue(value: String?): Double {
        return if (TextUtils.isEmpty(value)) 0.0 else value?.toDouble()!!
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
        val yf = p0!! + p1!! + p2!!
        val hj = p0 + p1 + p2 + p3!! + p4!!
        tvAllMoney.text = "运费：$yf 合计(含代收中转):$hj"
    }

    private fun showBzdwListDialog() {

        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装单位")
        listDialog.setItems(items) { _, which ->
            bzdwStr = which
            tvBzdw.text = items[which]
        }
        listDialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> createOrder()
            R.id.tvFwhy -> startActivityForResult<PremiumActivity>(
                200,
                "fk" to waybillModel?.returnmoney,
                "hdfs" to waybillModel?.copycount,
                "tzfh" to waybillModel?.waitnotify
            )
            R.id.tvCc -> startActivityForResult<VehicleActivity>(201)
            R.id.tvBzdw -> showBzdwListDialog()
            R.id.ivFhd -> startActivityForResult<ShipmentsActivity>(195)
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShr -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivGys -> startActivityForResult<SupplierActivity>(197)
            R.id.ivLxr -> startActivityForResult<ConsignerActivity>(196)
            R.id.btnPrintXp -> startActivityForResult<ConfigPrintActivity>(194)
            R.id.btnPrintBq -> {
            }
            R.id.btnDelete -> delete()
        }
    }

    private fun delete() {
        val map = hashMapOf<String, Any>(
            "clientCategory" to 4,
            "clientVersion" to "1.0",
            "id" to loginModel?.id!!,
            "isDel" to 1,
            "mobile" to loginModel?.mobile!!,
            "orderid" to waybillModel?.id!!,
            "sessionId" to sessionid!!
        )
        HttpNetUtils.getInstance().getManager()?.wleditOrDel(map)?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                }
            })
    }


    private fun createOrder() {
        val fhdStr = etFhd.text.toString()
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

        if (TextUtils.isEmpty(jbyfStr)) {
            toast("基本运费不能为空")
            return
        }

        val order = JSONObject(
            hashMapOf(
                "copycount" to hdfsStr, //回单份数
                "agentmoney" to zzfStr, //中转费
                "shipfee" to 0, //运费 （总计
                "serviceName" to gysStr,//供应商
                "dispatchfee" to psfStr, //派送费
                "receivername" to shrStr,//收货人
                "productweight" to zlStr,//重量
                "receivepoint" to mddStr, //目的地
                "shipfeesendpay" to zzfStr,//中转费
                "costFee" to cbStr, //成本
                "senderphone" to fhrModel?.mobile,//发货人电话
                "shipfeestate" to "0",//运费支付，0欠款，1已付
                "shipfeepaytype" to shipfeepaytype, //支付方式
                "sendername" to fhrStr,//发货人
                "receiveraddress" to shrModel?.addr, //收货人的地址
                "waitnotify" to tzfh,//等通知放货，0否，1是
                "productno" to kddhStr,//快递单号
                "baseshipfee" to jbyfStr, //基本运费
                "insurancefee" to bfStr, //保费
                "recno" to bzdwStr, //包装单位
                "productcount" to hwslStr,//货物数量
                "recway" to recway,//收货方式，0自提，1派送
                "productsize" to tjStr,//体积
                "receiverphone" to shrModel?.mobile,//收货人电话
                "productdescript" to hwmcStr, //货物名称
                "returnmoney" to fkStr,//返款
                "carname" to ccStr!!,//车次
                "comment" to bzStr, //备注
                "senderaddress" to fhdStr,//发货人地址
                "shipFeeState" to if (shipfeepaytype == 0) "1" else "0", //0未付，1付清
                "id" to waybillModel?.id!!
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
                    toast(data?.msg!!)
//                    if ("success" == data.code)
//                        finish()
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
            }
            197 -> {
                val itemStr = data.getStringExtra("data")
                etGys.setText(itemStr)
            }
            196 -> {
                fhrModel = data.getSerializableExtra("data") as ConsigneeModel
                etFhr.setText(fhrModel?.name)
            }
            195 -> {
                val destinationModel = data.getSerializableExtra("data") as DestinationModel
                etFhd.setText(destinationModel.receivepoint)
            }

            201 -> {
                ccStr = data.getStringExtra("data")
                tvCc.text = ccStr
            }
        }
    }
}

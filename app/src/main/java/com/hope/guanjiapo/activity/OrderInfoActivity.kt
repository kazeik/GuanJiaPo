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
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.Utils.formatDouble
import kotlinx.android.synthetic.main.activity_order_info.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.json.JSONObject


class OrderInfoActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_order_info
    }

    private var ccStr: String? = ""
    private var bzdwStr: Int? = 0
    private var recway: Int = 0
    private var shipfeepaytype: Int = 0
    private var fkStr: String = ""
    private var hdfsStr: String = ""
    private var tzfh: Int = 0

    private var fhrModel: ConsigneeModel? = null //发货人
    private var shrModel: ConsigneeModel? = null //货人

    private var change: Boolean? = false
    private var flag: Boolean? = false

    private var yf: Double? = 0.0

    private val hwmc: ArrayList<String> by lazy { arrayListOf<String>() }

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
        ivHwmc.setOnClickListener(this)

        flag = PreferencesUtils.getBoolean(this, "auto")
        change = intent.getBooleanExtra("change", false)

        val tempHwmc = PreferencesUtils.getString(this, "hwmc")
        tempHwmc?.split(",")?.forEach { hwmc.add(it) }

        mPayTypeGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbXf -> shipfeepaytype = 0
                R.id.rbYj -> shipfeepaytype = 1
                R.id.rbTf -> shipfeepaytype = 2
            }
        }
        msgShfs.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            when (i) {
                R.id.rbZt -> recway = 0
                R.id.rbPs -> recway = 1
            }
        }

        if (flag!!) {
            tvCc.text = PreferencesUtils.getString(this, "cc")
            etMdd.setText(PreferencesUtils.getString(this, "mdd"))
            val bzdw = PreferencesUtils.getInt(this, "bzdw", 0)
            when (bzdw) {
                0 -> rbZt.isChecked
                1 -> rbPs.isChecked
            }
            etHwsl.setText("1")
            val type = PreferencesUtils.getInt(this, "zffs")
            when (type) {
                0 -> rbXf.isChecked = true
                1 -> rbYj.isChecked = true
                2 -> rbTf.isChecked = true
            }
            etFhd.setText(PreferencesUtils.getString(this, "fhd"))
            etFhr.setText(PreferencesUtils.getString(this, "fhr"))
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
        tvAllMoney.text = "运费：${formatDouble(yf!!)} 合计(含代收中转):${formatDouble(hj)}"
    }


    private fun showBzdwListDialog() {
        val items = resources.getStringArray(R.array.bzdw)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装单位")
        listDialog.setItems(items) { dialog, which ->
            bzdwStr = which
            tvBzdw.text = items[which]
        }
        listDialog.show()
    }

    private fun showHwmcListDialog() {
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择货物名称")
        listDialog.setItems(hwmc.toTypedArray()) { _, which ->
            etHwmc.setText(hwmc.get(which))
        }
        listDialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {
                createOrder()
            }
            R.id.tvFwhy -> startActivityForResult<PremiumActivity>(
                200,
                "fk" to fkStr,
                "hdfs" to hdfsStr,
                "tzfh" to tzfh
            )
            R.id.tvCc -> startActivityForResult<VehicleActivity>(201, "a" to true)
            R.id.tvBzdw -> showBzdwListDialog()
            R.id.ivFhd -> startActivityForResult<ShipmentsActivity>(195)
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShr -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivGys -> startActivityForResult<SupplierActivity>(197, "a" to true)
            R.id.ivLxr -> startActivityForResult<ConsignerActivity>(196)
            R.id.ivHwmc -> showHwmcListDialog()
        }
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

        if (!hwmc.contains(hwmcStr))
            hwmc.add(hwmcStr)

        var temp = ""
        hwmc.forEach {
            temp += "$it,"
        }

        temp = temp.substring(0, temp.length - 1)
        PreferencesUtils.putString(this, "hwmc", temp)

        if (flag!!) {
            PreferencesUtils.putString(this, "cc", ccStr!!)
            PreferencesUtils.putString(this, "mdd", mddStr)
            PreferencesUtils.putInt(this, "bzdw", bzdwStr!!)
            PreferencesUtils.putString(this, "hwsl", hwslStr)
            PreferencesUtils.putInt(this, "zffs", shipfeepaytype)
            PreferencesUtils.putString(this, "fhd", fhdStr)
            PreferencesUtils.putString(this, "fhr", fhrStr)
        }


        val order = JSONObject(
            hashMapOf(
                "copycount" to hdfsStr, //回单份数
                "agentmoney" to dskStr, //代收款
                "shipfee" to yf, //运费 （总计
                "serviceName" to gysStr,//供应商
                "dispatchfee" to psfStr, //派送费
                "receivername" to shrStr,//收货人
                "productweight" to zlStr,//重量
                "receivepoint" to mddStr, //目的地
                "shipfeesendpay" to zzfStr,//中转费
                "costFee" to cbStr, //成本
                "senderphone" to fhrModel?.mobile,//发货人电话
                "shipfeepaytype" to shipfeepaytype, //支付方式
                "sendername" to fhrStr,//发货人
                "receiveraddress" to shrModel?.addr, //收货人的地址
                "waitnotify" to tzfh,//等通知放货，0否，1是
                "productno" to kddhStr,//快递单号
                "baseshipfee" to jbyfStr, //基本运费
                "insurancefee" to bfStr, //保费
                "recno" to bzdwStr,
                "productcount" to hwslStr,//货物数量
                "recway" to recway,//收货方式，0自提，1派送
                "productsize" to tjStr,//体积
                "receiverphone" to shrModel?.mobile,//收货人电话
                "productdescript" to hwmcStr, //货物名称
                "returnmoney" to fkStr,//返款
                "carname" to ccStr!!,//车次
                "comment" to bzStr, //备注
                "senderaddress" to fhdStr,//发货人地址
//                "shipfeestate" to if (shipfeepaytype == 0) "1" else "0",
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
                    toast(data?.msg!!)
                    if (data.code == "success") {
                        startActivity<PrintOrderInfoActivity>("data" to data.data)
                        finish()
                    }
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                val destinationModel = data.getStringExtra("data")
                etFhd.setText(destinationModel)
            }

            201 -> {
                ccStr = data.getStringExtra("data")
                tvCc.text = ccStr
            }
        }
    }
}

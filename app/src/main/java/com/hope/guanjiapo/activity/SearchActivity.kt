package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.model.StaffModel
import com.hope.guanjiapo.utils.TimeUtil
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivityForResult

class SearchActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        dsk = if (isChecked) 1 else 0
    }

    private val REQUEST_CODE = 10001
    override fun getLayoutView(): Int {
        return R.layout.activity_search
    }

    private var startTime: String? = ""
    private var ywy: String? = ""
    private var endTime: String? = ""
    private var ccStr: String? = ""
    private var fhd: String? = ""
    private var dsk: Int? = 0
    private var orderstatus: Int? = 0
    private var shrModel: ConsigneeModel? = null //货人
    private var fhrModel: ConsigneeModel? = null //发货人
    override fun initData() {
        tvTitle.setText(R.string.search)
        ivBackup.setOnClickListener(this)
        tvKsrq.setOnClickListener(this)
        tvJsrq.setOnClickListener(this)
        tvCc.setOnClickListener(this)
        tvYwy.setOnClickListener(this)
        ivMdd.setOnClickListener(this)
        ivShdh.setOnClickListener(this)
        tvFhd.setOnClickListener(this)
        ivFhdh.setOnClickListener(this)
        ivDh.setOnClickListener(this)
        tvYdzt.setOnClickListener(this)
        cbDsk.setOnCheckedChangeListener(this)
        btnSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.ivDh -> startActivityForResult<CaptureActivity>(REQUEST_CODE)
            R.id.tvKsrq -> showTime(true)
            R.id.tvJsrq -> showTime(false)
            R.id.tvCc -> startActivityForResult<VehicleActivity>(201, "a" to true)
            R.id.tvYwy -> startActivityForResult<StaffActivity>(194)
            R.id.tvYdzt -> showOrderStatusDialog()
            R.id.tvFhd -> startActivityForResult<ShipmentsActivity>(195)
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShdh -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivFhdh -> startActivityForResult<ConsignerActivity>(197)
            R.id.btnSearch -> {
                val map = hashMapOf<String, Any>()
                val tempOrderId = etDh.text.toString().trim()
                val recePhone = etShdh.text.toString().trim()
                val recepoint = etMdd.text.toString().trim()
                val fhr = etFhdh.text.toString().trim()
                if (!TextUtils.isEmpty(tempOrderId)) map["orderid"] = tempOrderId
                if (!TextUtils.isEmpty(recePhone)) map["receiverphone"] = recePhone
                if (!TextUtils.isEmpty(recepoint)) map["receivepoint"] = recepoint
                if (!TextUtils.isEmpty(fhr)) map["senderphone"] = fhr
                if (!TextUtils.isEmpty(endTime)) map["endDate"] = endTime!!
                if (0 != dsk) map["agentmoney"] = dsk!!
                if (!TextUtils.isEmpty(ccStr)) map["carname"] = ccStr!!
                if (0 != orderstatus) map["oderstate"] = orderstatus!!
                if (!TextUtils.isEmpty(ywy)) map["operatorMobile"] = ywy!!
                if (!TextUtils.isEmpty(fhd)) map["senderaddress"] = fhd!!
                if (!TextUtils.isEmpty(startTime)) map["startDate"] = startTime!!
                val intt = Intent()
                intt.putExtra("data", map)
                setResult(119, intt)
                finish()
            }
        }
    }

    private fun showTime(start: Boolean) {
        val dialogYearMonthDay = TimePickerDialog.Builder()
            .setType(Type.YEAR_MONTH_DAY)
            .setTitleStringId("请选择时间")
            .setThemeColor(R.color.actionbar_color)
            .setCallBack { _, millseconds ->
                val tempTime = TimeUtil.getDayByType(millseconds, TimeUtil.DATE_YMS)
                if (start) {
                    startTime = tempTime
                    tvKsrq.text = tempTime
                } else {
                    endTime = tempTime
                    tvJsrq.text = tempTime
                }
            }
            .build()
        dialogYearMonthDay.show(supportFragmentManager, "YEAR_MONTH_DAY")
    }

    private fun showOrderStatusDialog() {
        val items = resources.getStringArray(R.array.orderstatus)
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择运单状态")
        listDialog.setItems(items) { dialog, which ->
            dialog.dismiss()
            orderstatus = which
            tvYdzt.text = items?.get(which)
        }
        listDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            REQUEST_CODE -> {
                val bundle = data.extras ?: return
                val result = bundle.getString(CodeUtils.RESULT_STRING)
                etDh.setText(result)
            }
            195 -> {
//                val destinationModel = data.getSerializableExtra("data") as DestinationModel
                fhd = data.getStringExtra("data")
                tvFhd.text = fhd
            }
            199 -> {
                val item = data.getSerializableExtra("data") as DestinationModel
                etMdd.setText(item.receivepoint)
            }
            198 -> {
                shrModel = data.getSerializableExtra("data") as ConsigneeModel
                etShdh.setText(shrModel?.mobile)
            }
            197 -> {
                fhrModel = data.getSerializableExtra("data") as ConsigneeModel
                etFhdh.setText(fhrModel?.mobile)
            }
            194 -> {
                val staffModel = data.getSerializableExtra("data") as StaffModel
                tvYwy.text = staffModel.userName
                ywy = staffModel.mobile
            }
            201 -> {
                ccStr = data.getStringExtra("data")
                tvCc.text = ccStr
            }
        }
    }
}

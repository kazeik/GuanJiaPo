package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import kotlinx.android.synthetic.main.activity_add_consignee.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import org.json.JSONObject

class AddConsigneeActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnAdd -> addcontent()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_add_consignee
    }

    private var type: Boolean = false
    private var consigneeModel: ConsigneeModel? = null
    @SuppressLint("SetTextI18n")
    override fun initData() {

        ivBackup.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        type = intent.getBooleanExtra("type", false)
        consigneeModel = intent.getSerializableExtra("data") as? ConsigneeModel
        tvTitle.setText(if (type) R.string.changecontacts else R.string.contacts)
        if (type) {
            tvId.text = "id：${consigneeModel?.id}"
            etName.setText(consigneeModel?.name)
            etPhone.setText(consigneeModel?.mobile)
            etPhone.isEnabled = false
            etAdd.setText(consigneeModel?.addr)
        }
    }

    private fun addcontent() {
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val address = etAdd.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast(R.string.error_name_empty)
            return
        }
        if (TextUtils.isEmpty(phone)) {
            toast(R.string.error_email_empty)
            return
        }
        if (TextUtils.isEmpty(address)) {
            toast(R.string.error_address_empty)
            return
        }

        val stat = if (type) "0" else "1"
        val params = JSONObject(
            hashMapOf(
                "operatorMobile" to phone,
                "mobile" to phone,
                "addr" to address,
                "type" to "0",
                "name" to name,
                "id" to consigneeModel?.id!!
            )
        ).toString()
        val data =
            "clientCategory=4&clientVersion=1.0&id=${loginModel?.id}&isadd=$stat&mobile=${loginModel?.mobile}&sessionId=$sessionid&param=$params"
        val requestBody = RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data
        )
        HttpNetUtils.getInstance().getManager()?.addoreditex(
            requestBody
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
            }
        })
    }

}

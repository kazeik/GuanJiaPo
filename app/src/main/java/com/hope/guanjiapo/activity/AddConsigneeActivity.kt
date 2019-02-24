package com.hope.guanjiapo.activity

import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import kotlinx.android.synthetic.main.activity_add_consignee.*
import kotlinx.android.synthetic.main.view_title.*
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

    private var type: Int = 0
    override fun initData() {
        tvTitle.text = "添加联系人"
        ivBackup.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        type = intent.getIntExtra("type", 0)
    }

    private fun addcontent() {
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val address = etAdd.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast("名称不能为空")
            return
        }
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空")
            return
        }
        if (TextUtils.isEmpty(address)) {
            toast("地址不能为空")
            return
        }

        HttpNetUtils.getInstance().getManager()?.addoreditex(
            hashMapOf(
                "isadd" to 1,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!,
                "param" to JSONObject(
                    hashMapOf(
                        "operatorMobile" to phone,
                        "mobile" to phone,
                        "addr" to address,
                        "type" to type,
                        "name" to name
                    )
                )
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
            }
        })
    }

}
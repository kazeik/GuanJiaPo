package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import kotlinx.android.synthetic.main.activity_add_destination.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class AddDestinationActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnAdd -> if (type) editcontent() else addcontent()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_add_destination
    }

    private var type: Boolean = false
    private var destinationModel: DestinationModel? = null
    @SuppressLint("SetTextI18n")
    override fun initData() {
        ivBackup.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        type = intent.getBooleanExtra("change", false)
        destinationModel = intent.getSerializableExtra("data") as DestinationModel
        tvId.text = "id:${destinationModel?.id}"
        tvTitle.text = if (type) getString(R.string.changeadd) else getString(R.string.addaddress)
        if (type) {
            etName.setText(destinationModel?.receivepoint)
        }
    }

    private fun addcontent() {
        val name = etName.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast(R.string.error_name_empty)
            return
        }

        HttpNetUtils.getInstance().getManager()?.addcompanyPoint(
            hashMapOf(
                "isadd" to 1,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!,
                "param" to JSONObject(
                    hashMapOf(
                        "receivepoint" to "England",
                        "type" to 0
                    )
                )
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
            }
        })
    }

    private fun editcontent() {
        val name = etName.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast(R.string.error_name_empty)
            return
        }

        HttpNetUtils.getInstance().getManager()?.editcompanyPoint(
            hashMapOf(
                "isadd" to 0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!,
                "param" to JSONObject(
                    hashMapOf(
                        "receivepoint" to "china",
                        "type" to 0,
                        "id" to destinationModel?.id
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

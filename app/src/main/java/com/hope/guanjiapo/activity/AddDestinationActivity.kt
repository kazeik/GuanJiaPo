package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
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
            R.id.btnAdd -> addcontent()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_add_destination
    }

    private var type: Boolean = false
    @SuppressLint("SetTextI18n")
    override fun initData() {
        ivBackup.setOnClickListener(this)
        btnAdd.setOnClickListener(this)

        type = intent.getBooleanExtra("change", false)
        tvId.text = "id:${intent.getIntExtra("id", 0)}"
        tvTitle.text = if (type) "修改地区" else "添加地区"
    }

    private fun addcontent() {
        val name = etName.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast("名称不能为空")
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

}

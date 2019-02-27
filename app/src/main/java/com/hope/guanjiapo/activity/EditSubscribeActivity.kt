package com.hope.guanjiapo.activity

import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.SubscribeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import kotlinx.android.synthetic.main.activity_edit_subscribe.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class EditSubscribeActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_edit_subscribe
    }

    private var subscribeModel: SubscribeModel? = null
    override fun initData() {
        subscribeModel = intent.getSerializableExtra("data") as SubscribeModel

        tvTitle.setText(R.string.subscribe)
        tvTitleRight.setText(R.string.tozs)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvFwhy.setOnClickListener(this)
        btnDelete.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {
            }
            R.id.tvFwhy -> startActivity<PremiumActivity>()
            R.id.btnDelete -> delete()
        }
    }

    private fun delete() {
        HttpNetUtils.getInstance().getManager()?.wxdelete(
            hashMapOf(
                "isDel" to 1,
                "orderid" to subscribeModel?.id!!,
                "sessionId" to loginModel?.sessionid!!,
                "mobile" to loginModel?.mobile!!
            )
        )
            ?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                }
            })
    }
}

package com.hope.guanjiapo.activity

import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.MD5Utils
import kotlinx.android.synthetic.main.activity_change_pass.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast

class ChangePassActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnChangePass -> changePass()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_change_pass
    }

    override fun initData() {
        tvTitle.text = "密码修改"
        ivBackup.setOnClickListener(this)
    }

    private fun changePass() {
        val oldPass = etOldPass.text.toString()
        val newPass = etNewPass.text.toString()
        val subPass = etSurePass.text.toString()

        if (TextUtils.isEmpty(oldPass)) {
            toast("原密码不能为空")
            return
        }
        if (TextUtils.isEmpty(newPass)) {
            toast("新密码不能为空")
            return
        }
        if (TextUtils.isEmpty(subPass)) {
            toast("重复密码不能为空")
            return
        }

        if (!TextUtils.equals(newPass, subPass)) {
            toast("两次密码不一致")
            return
        }

        val old = MD5Utils.MD5Encode(oldPass, "utf-8")
        val new = MD5Utils.MD5Encode(newPass, "utf-8")
        HttpNetUtils.getInstance().getManager()?.wlreg(
            hashMapOf(
                "id" to ApiUtils.loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to ApiUtils.loginModel?.mobile!!,
                "sessionId" to ApiUtils.loginModel?.sessionid!!,
                "oldUserPwd" to old,
                "password" to new
            )
        )?.compose(NetworkScheduler.compose())?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
            override fun onSuccess(data: BaseModel<String>?) {
                toast(data?.msg!!)
            }
        })
    }
}

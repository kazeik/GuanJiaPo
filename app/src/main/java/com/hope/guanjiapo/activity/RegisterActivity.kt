package com.hope.guanjiapo.activity

import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.LoginModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.utils.MD5Utils
import com.hope.guanjiapo.view.JFDialog
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class RegisterActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnRegister -> register()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_register
    }

    private var mobile: String? = ""
    private var pass: String? = ""
    override fun initData() {
        tvTitle.text = "注册"

        ivBackup.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }

    private fun register() {
        mobile = etAccount.text.toString()
        pass = etPass.text.toString()
        val compane = etCompane.text.toString()

        if (TextUtils.isEmpty(mobile)) {
            toast(R.string.error_phone_empty)
            return
        }
        if (TextUtils.isEmpty(pass)) {
            toast(R.string.error_password_empty)
            return
        }
        if (TextUtils.isEmpty(compane)) {
            toast(R.string.error_company_empty)
            return
        }
        HttpNetUtils.getInstance().getManager()?.register(
            hashMapOf(
                "companyName" to compane, "mobile" to mobile!!, "password" to MD5Utils.MD5Encode(pass!!, "utf-8")
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<LoginModel>>(this) {
                override fun onSuccess(data: BaseModel<LoginModel>?) {
                    toast(R.string.register_ok)
                    relogin(0)
                }
            })
    }

    private fun relogin(isFore: Int) {
        HttpNetUtils.getInstance().getManager()?.login(
            hashMapOf(
                "account" to mobile!!,
                "ignore" to true,
                "isForce" to isFore,
                "mobile" to mobile!!,
                "password" to MD5Utils.MD5Encode(pass!!, "utf-8")
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<LoginModel>>(this) {
                override fun onSuccess(data: BaseModel<LoginModel>?) {
                    loginModel = data?.data!!
                    sessionid = data.sessionId!!
                    startActivity<MainActivity>()
                }

                override fun reLogin() {
                    super.reLogin()
                    JFDialog.Builder(this@RegisterActivity).setContentText(getString(R.string.relogin))
                        .setTitleText(getString(R.string.title))
                        .setCancelText(getString(R.string.cancel)).setSureText(getString(R.string.sure))
                        .setDialogSureListener { relogin(1) }.create().show()
                }
            })
    }
}

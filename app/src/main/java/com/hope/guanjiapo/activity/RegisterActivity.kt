package com.hope.guanjiapo.activity

import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.BuildConfig
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.LoginModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.MD5Utils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.view_title.*
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

    override fun initData() {
        tvTitle.text = "注册"

        ivBackup.setOnClickListener(this)
        btnRegister.setOnClickListener(this)

        if(BuildConfig.DEBUG){
            etAccount.setText("18573183417")
            etCompane.setText("123456")
            etPass.setText("123456")
        }
    }

    private fun register() {
        val mobile = etAccount.text.toString()
        val pass = etPass.text.toString()
        val compane = etCompane.text.toString()

        if (TextUtils.isEmpty(mobile)) {
            toast("手机号不能为空")
            return
        }
        if (TextUtils.isEmpty(pass)) {
            toast("密码不能为空")
            return
        }
        if (TextUtils.isEmpty(compane)) {
            toast("公司名称不能为空")
            return
        }
        val map = hashMapOf(
            "clientCategory" to 3
            , "clientVersion" to "1.0.0"
            , "companyName" to compane, "mobile" to mobile, "password" to MD5Utils.MD5Encode(pass, "utf-8")
        )
        val body = HttpNetUtils.getInstance().getParamsBody(map)
        HttpNetUtils.getInstance().getManager()?.register(body)
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<LoginModel>>(this) {
                override fun onSuccess(data: BaseModel<LoginModel>?) {
                    toast(data?.msg!!)
                }
            })
    }
}

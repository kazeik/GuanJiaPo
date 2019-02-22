package com.hope.guanjiapo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.LoginModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.MD5Utils
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.view.JFDialog
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.http.FieldMap


class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCallPhone ->//
                JFDialog.Builder(this).setTitleText(getString(R.string.title)).setCancelText(getString(R.string.cancel)).setSureText(
                    getString(R.string.sure)
                ).setDialogSureListener {
                    startActivity(
                        Intent().setAction(Intent.ACTION_CALL).setData(Uri.parse(getString(R.string.phonenum)))
                    )
                }.setContentText(getString(R.string.callphone)).create().show()
            R.id.btnLogin -> login()
            R.id.btnRegister -> startActivity<RegisterActivity>()
        }
    }

    @SuppressLint("CheckResult")
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            RxPermissions(this).request(
                Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.CALL_PHONE
            )
                .subscribe { permission ->
                    if (!permission) {
                        toast("请同意权限")
                        finish()
                    }
                }
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
        tvCallPhone.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)

        etPhone.setText("15988879319")
        password.setText("1234")

        checkPermission()
    }

    private fun login() {
        val phone = etPhone.text.toString()
        val pass = password.text.toString()
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空")
            return
        }

        if (TextUtils.isEmpty(pass)) {
            toast("密码不能为空")
            return
        }


        if (cbSavePass.isChecked) {
            PreferencesUtils.putString(this, "user", phone)
            PreferencesUtils.putString(this, "pass", pass)
        } else {
            PreferencesUtils.putString(this, "user", "")
            PreferencesUtils.putString(this, "pass", "")
        }

        HttpNetUtils.getInstance().getManager()?.login(
            hashMapOf(
                "account" to phone,
                "clientCategory" to 3,
                "clientVersion" to "1",
                "id" to "9d5fd200e7a2467dab3f2228353b0d2d",
                "ignore" to true,
                "isForce" to 0,
                "mobile" to phone,
                "password" to MD5Utils.MD5Encode(pass, "utf-8"),
                "sessionId" to 0
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<LoginModel>>(this) {
                override fun onSuccess(data: BaseModel<LoginModel>?) {

                    startActivity<MainActivity>()
                }
            })
    }

}

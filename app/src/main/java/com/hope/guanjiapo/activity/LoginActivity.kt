package com.hope.guanjiapo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import android.net.Uri
import android.os.Build
import com.hope.guanjiapo.view.JFDialog
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.internal.operators.observable.ObservableBlockingSubscribe.subscribe
import org.jetbrains.anko.toast


class LoginActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
    }

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
                , Manifest.permission.CALL_PHONE)
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
        cbSavePass.setOnCheckedChangeListener(this)
        tvCallPhone.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)

        checkPermission()
    }

    private fun login() {}

}

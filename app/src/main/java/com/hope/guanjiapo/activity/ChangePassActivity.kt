package com.hope.guanjiapo.activity

import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
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

    }
}

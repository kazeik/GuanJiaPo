package com.hope.guanjiapo.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import kotlinx.android.synthetic.main.view_title.*

class PayActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_pay
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnPay,
            R.id.btnRePay -> gtoto()
        }
    }

    override fun initData() {
        tvTitle.setText(R.string.payaccount)
        ivBackup.setOnClickListener(this)
    }

    private fun gtoto() {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.data = Uri.parse("https://item.taobao.com/item.htm?id=555129970426")
        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
        startActivity(intent)
    }
}

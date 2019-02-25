package com.hope.guanjiapo.activity

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
            R.id.btnPay -> {
            }
            R.id.btnRePay -> {
            }
        }
    }

    override fun initData() {
        tvTitle.setText(R.string.payaccount)
        ivBackup.setOnClickListener(this)
    }

}

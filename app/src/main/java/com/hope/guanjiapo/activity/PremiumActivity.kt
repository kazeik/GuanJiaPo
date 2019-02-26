package com.hope.guanjiapo.activity

import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.model.SubscribeModel
import kotlinx.android.synthetic.main.view_title.*

class PremiumActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_edit_subscribe
    }

    private var subscribeModel:SubscribeModel?= null
    override fun initData() {
        subscribeModel = intent.getSerializableExtra("data") as SubscribeModel

        tvTitle.setText(R.string.fwhy)
        ivBackup.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {}
            R.id.tvFwhy->{}
        }
    }


}

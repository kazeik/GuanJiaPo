package com.hope.guanjiapo.activity

import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.model.SubscribeModel
import kotlinx.android.synthetic.main.activity_edit_subscribe.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class OrderInfoActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_order_info
    }

    override fun initData() {
        tvTitle.setText(R.string.orderinfo)
        tvTitleRight.setText(R.string.save)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvFwhy.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {
            }
            R.id.tvFwhy -> startActivity<PremiumActivity>()
        }
    }


}

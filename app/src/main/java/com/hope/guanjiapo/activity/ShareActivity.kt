package com.hope.guanjiapo.activity

import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import kotlinx.android.synthetic.main.view_title.*

class ShareActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_share
    }

    override fun initData() {
        tvTitle.setText(R.string.share)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
        }
    }
}

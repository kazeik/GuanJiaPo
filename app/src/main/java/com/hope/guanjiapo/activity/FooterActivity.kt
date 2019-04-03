package com.hope.guanjiapo.activity

import android.content.Intent
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_footer.*
import kotlinx.android.synthetic.main.view_title.*

class FooterActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {
                val msg = etFooter.text.toString()
                PreferencesUtils.putString(this@FooterActivity, "footer", msg)
                val intt = Intent()
                intt.putExtra("data", msg)
                setResult(109, intt)
                finish()
            }
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_footer
    }

    override fun initData() {
        ivBackup.setOnClickListener(this)
        tvTitle.text = "页脚"
        tvTitleRight.setOnClickListener(this)
        tvTitleRight.visibility = View.VISIBLE
        tvTitleRight.text = "保存"

        etFooter.setText(PreferencesUtils.getString(this, "footer"))
    }

}

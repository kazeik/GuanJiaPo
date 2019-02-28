package com.hope.guanjiapo.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class ConfigPrintActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnCheck -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse("https//h5.m.taobao.com/awp/core/detail.htm?id=552270159252")
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
                startActivity(intent)
            }
            R.id.btnSetType -> startActivity<ConfigPrintSetActivity>()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_config_print
    }

    override fun initData() {
        tvTitle.setText(R.string.set_pring)
        ivBackup.setOnClickListener(this)
    }

}

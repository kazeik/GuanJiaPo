package com.hope.guanjiapo.activity

import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import kotlinx.android.synthetic.main.activity_config_print_set.*
import kotlinx.android.synthetic.main.view_title.*

class ConfigPrintSetActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.rlItem1 -> finish()
            R.id.rlItem2 -> finish()
            R.id.rlItem3 -> finish()
            R.id.rlItem4 -> finish()
            R.id.rlItem5 -> finish()
            R.id.rlItem6 -> finish()
            R.id.rlItem7 -> finish()
            R.id.rlQrcode -> finish()
            R.id.rlzdy -> finish()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_config_print_set
    }

    override fun initData() {
        tvTitle.setText(R.string.set_pring_type)
        ivBackup.setOnClickListener(this)
        rlItem1.setOnClickListener(this)
        rlItem2.setOnClickListener(this)
        rlItem3.setOnClickListener(this)
        rlItem4.setOnClickListener(this)
        rlItem5.setOnClickListener(this)
        rlItem6.setOnClickListener(this)
        rlItem7.setOnClickListener(this)
        rlQrcode.setOnClickListener(this)
        rlzdy.setOnClickListener(this)
    }

}

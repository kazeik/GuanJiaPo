package com.hope.guanjiapo.activity

import android.content.Intent
import android.view.View
import android.widget.RadioGroup
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_premium.*
import kotlinx.android.synthetic.main.view_title.*

class PremiumActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_premium
    }

    private var tzfh: Int = 0
    override fun initData() {
        tvTitle.setText(R.string.fwhy)
        ivBackup.setOnClickListener(this)
        msgShfs.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            when (i) {
                R.id.rbNo -> tzfh = 0
                R.id.rbSure -> tzfh = 1
            }
        }

        val fk = intent.getStringExtra("fk")
        val hdfs = intent.getStringExtra("hdfs")
        val tzfh = intent.getIntExtra("tzfh", 0)
        if (tzfh == 0) {
            rbNo.isChecked = true
        } else {
            rbSure.isChecked = true
        }
        etFk.setText(fk)
        etHdfs.setText(hdfs)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> {
                val fkStr = etFk.text.toString()
                val hdfsStr = etHdfs.text.toString()
                val intent = Intent()
                intent.putExtra("fk", fkStr)
                intent.putExtra("hdfs", hdfsStr)
                intent.putExtra("tzfh", tzfh)
                setResult(200, intent)
                finish()
            }
        }
    }


}

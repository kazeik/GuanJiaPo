package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_config_print_set.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivityForResult

class ConfigPrintSetActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.rlItem1 -> showDialog(printArr, 1)
            R.id.rlItem2 -> showDialog(alertArr, 2)
            R.id.rlItem3 -> showDialog(alertArr, 3)
            R.id.rlItem4 -> showDialog(alertArr, 4)
            R.id.rlItem5 -> showDialog(printtypeArr, 5)
            R.id.rlItem6 -> showDialog(printtypesArr, 6)
            R.id.rlItem7 -> startActivityForResult<FooterActivity>(109)
            R.id.rlQrcode -> {
            }
            R.id.rlzdy -> {
            }
        }
    }

    private val printArr: Array<String> by lazy { resources.getStringArray(R.array.print) }
    private val alertArr: Array<String> by lazy { resources.getStringArray(R.array.alert) }
    private val printtypeArr: Array<String> by lazy { resources.getStringArray(R.array.printtype) }
    private val printtypesArr: Array<String> by lazy { resources.getStringArray(R.array.printtypes) }
    private fun showDialog(items: Array<String>, index: Int) {
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择运单状态")
        listDialog.setItems(items) { dialog, which ->
            dialog.dismiss()
            val itemstr = items.get(which)
            when (index) {
                1 -> tvItem2.text = itemstr
                2 -> tvItem3.text = itemstr
                3 -> tvItem4.text = itemstr
                4 -> tvItem5.text = itemstr
                5 -> tvItem6.text = itemstr
                6 -> tvItem7.text = itemstr
            }
        }
        listDialog.show()
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

        tvItem2.text = printArr[0]
        tvItem3.text = alertArr[0]
        tvItem4.text = alertArr[1]
        tvItem5.text = alertArr[1]
        tvItem6.text = printtypeArr[0]
        tvItem7.text = printtypesArr[0]
        tvItem8.text = PreferencesUtils.getString(this, "footer")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data) return
        when (requestCode) {
            109 -> {
                tvItem8.text = data.getStringExtra("data")
            }
        }
    }
}

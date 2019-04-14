package com.hope.guanjiapo.activity

import android.app.AlertDialog
import android.content.Context
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
            R.id.rlItem1 -> showDialog(printArr, 1, "请选择打印机宽度")
            R.id.rlItem2 -> showDialog(alertArr, 2, "保存后自动打印")
            R.id.rlItem3 -> showDialog(alertArr, 3, "不打印时间和版本号")
            R.id.rlItem4 -> showDialog(alertArr, 4, "隐藏便签电话")
            R.id.rlItem5 -> showDialog(printtypeArr, 5, "小票打印模板")
            R.id.rlItem6 -> showDialog(printtypesArr, 6, "标签打印模板")
            R.id.rlItem7 -> startActivityForResult<FooterActivity>(109)
            R.id.rlQrcode -> {
            }
            R.id.rlzdy -> startActivityForResult<ZdyActivity>(110, "zdy" to true)
        }
    }

    private val printArr: Array<String> by lazy { resources.getStringArray(R.array.print) }
    private val alertArr: Array<String> by lazy { resources.getStringArray(R.array.alert) }
    private val printtypeArr: Array<String> by lazy { resources.getStringArray(R.array.printtype) }
    private val printtypesArr: Array<String> by lazy { resources.getStringArray(R.array.printtypes) }
    private fun showDialog(items: Array<String>, index: Int, title: String) {
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle(title)
        listDialog.setItems(items) { dialog, which ->
            dialog.dismiss()
            val itemstr = items.get(which)
            var alt = ""
            when (index) {
                1 -> {
                    tvItem2.text = itemstr
                    alt = "kd"
                }
                2 -> {
                    tvItem3.text = itemstr
                    alt = "dy"
                }
                3 -> {
                    tvItem4.text = itemstr
                    alt = "bb"
                }
                4 -> {
                    tvItem5.text = itemstr
                    alt = "dh"
                }
                5 -> {
                    tvItem6.text = itemstr
                    alt = "xpmb"
                }
                6 -> {
                    tvItem7.text = itemstr
                    alt = "bqmb"
                }
            }
            PreferencesUtils.putString(this@ConfigPrintSetActivity, alt, itemstr)
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
        val perfent = getSharedPreferences("TrineaAndroidCommon", Context.MODE_PRIVATE)
        tvItem2.text = if (perfent.contains("kd")) PreferencesUtils.getString(this, "kd") else printArr[0]
        tvItem3.text = if (perfent.contains("dy")) PreferencesUtils.getString(this, "dy") else alertArr[0]
        tvItem4.text = if (perfent.contains("bb")) PreferencesUtils.getString(this, "bb") else alertArr[1]
        tvItem5.text = if (perfent.contains("dh")) PreferencesUtils.getString(this, "dh") else alertArr[1]
        tvItem6.text = if (perfent.contains("xpmb")) PreferencesUtils.getString(this, "xpmb") else printtypeArr[0]
        tvItem7.text = if (perfent.contains("bqmb")) PreferencesUtils.getString(this, "bqmb") else printtypesArr[0]
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

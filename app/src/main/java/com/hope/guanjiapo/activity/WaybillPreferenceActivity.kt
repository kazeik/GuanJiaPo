package com.hope.guanjiapo.activity


import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PreferenceAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*


class WaybillPreferenceActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.fragment_data
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
        }
    }

    override fun initData() {
        tvTitle.text = "运单默认设置"
        ivBackup.setOnClickListener(this)

        val itemArr = arrayOf("是否记忆上次运单")
        var flag = true
        val pref = getSharedPreferences("TrineaAndroidCommon", Context.MODE_PRIVATE)
        if (pref.contains("auto")) {
            flag = PreferencesUtils.getBoolean(this, "auto")
        }

        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.flag = flag
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(this, RecyclerView.VERTICAL))
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

    private val allItem by lazy { arrayListOf<AdapterItemModel>() }
    private val adapter by lazy { PreferenceAdapter<AdapterItemModel>() }
    override fun onItemEvent(pos: Int) {
        allItem.get(pos).flag = !allItem.get(pos).flag
        adapter.setDataEntityList(allItem)

        PreferencesUtils.putBoolean(this, "auto", allItem.get(pos).flag)
    }
}

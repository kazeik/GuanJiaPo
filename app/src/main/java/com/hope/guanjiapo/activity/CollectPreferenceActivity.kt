package com.hope.guanjiapo.activity


import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PreferenceAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.Utils.logs
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import java.util.*


class CollectPreferenceActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.fragment_data
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> {
                back()
            }
        }
    }

    private fun back() {
        check()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        back()
    }

    private val map: TreeMap<Int, Boolean> by lazy { TreeMap<Int, Boolean>() }
    override fun initData() {
        tvTitle.setText(R.string.perference)
        ivBackup.setOnClickListener(this)

        val itemArr = resources.getStringArray(R.array.hzdata)
        for (i in 0 until itemArr.size) {
            val item = AdapterItemModel()
            item.items = itemArr[i]
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }

        if (PreferencesUtils.getShared(this).contains("prefer")) {
            var tempstr = PreferencesUtils.getString(this, "prefer")
            if (!TextUtils.isEmpty(tempstr)) {
                tempstr = tempstr?.substring(0, tempstr.length - 1)
                val tempArr = tempstr?.split(",")
                tempArr?.forEach {
                    allItem[it.toInt()].flag = true
                    map[it.toInt()] = true
                }
            }
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
        map[pos] = allItem.get(pos).flag
    }

    private fun check() {
        var str = ""
        map.entries.forEach {
            if (it.value) {
                str += "${it.key},"
            }
        }
        PreferencesUtils.putString(this, "prefer", str)
    }

}

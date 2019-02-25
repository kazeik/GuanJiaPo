package com.hope.guanjiapo.activity


import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PreferenceAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*


class CollectPreferenceActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.fragment_data
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
        }
    }

    override fun initData() {
        tvTitle.text = "清单设置"
        ivBackup.setOnClickListener(this)

        val itemArr = resources.getStringArray(R.array.hzdata)
        for (i in 0 until itemArr.size) {
            val item = AdapterItemModel()
            item.items = itemArr[i]
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
    }


}

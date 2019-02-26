package com.hope.guanjiapo.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.SubscribeAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.SubscribeModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_search_recycler.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class EditSubscribeActivity : BaseActivity(), View.OnClickListener, OnItemEventListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_edit_subscribe
    }

    private val adapter: SubscribeAdapter<SubscribeModel> by lazy { SubscribeAdapter<SubscribeModel>() }
    private val allitem :ArrayList<SubscribeModel> by lazy { arrayListOf<SubscribeModel>() }
    override fun initData() {
        tvTitle.setText(R.string.subscribe)
        tvTitleRight.setText(R.string.tozs)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> {}
        }
    }

    override fun onItemEvent(pos: Int) {
        startActivity<EditSubscribeActivity>("data" to allitem[pos])
    }

}

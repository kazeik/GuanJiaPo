//package com.hope.guanjiapo.activity
//
//import android.app.Activity
//import android.os.Bundle
//import android.view.View
//import com.hope.guanjiapo.R
//import com.hope.guanjiapo.base.BaseActivity
//import com.hope.guanjiapo.iter.OnItemEventListener
//import kotlinx.android.synthetic.main.view_title.*
//import org.jetbrains.anko.startActivity
//
//class NewlyActivity : BaseActivity(),View.OnClickListener,OnItemEventListener {
//    override fun getLayoutView(): Int {
//        return R.layout.activity_subscribe
//    }
//
//    override fun initData() {
//        tvTitle.setText(R.string.newly)
//        tvTitleRight.setText(R.string.search)
//        tvTitleRight.visibility = View.VISIBLE
//        ivBackup.setOnClickListener(this)
//        tvTitleRight.setOnClickListener(this)
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.ivBackup -> finish()
//            R.id.tvTitleRight -> startActivity<AddConsigneeActivity>()
//        }
//    }
//
//    override fun onItemEvent(pos: Int) {
//    }
//
//}

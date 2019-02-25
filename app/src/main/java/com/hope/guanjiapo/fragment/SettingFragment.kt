package com.hope.guanjiapo.fragment


import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.hope.guanjiapo.R
import com.hope.guanjiapo.activity.ChangePassActivity
import com.hope.guanjiapo.activity.PreferenceActivity
import com.hope.guanjiapo.activity.StaffActivity
import com.hope.guanjiapo.adapter.SettingAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.view.JFDialog
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


/**
 * A simple [Fragment] subclass.
 *
 */
class SettingFragment : BaseFragment(), OnItemEventListener {
    private val allItem by lazy { arrayListOf<AdapterItemModel>() }
    private val adapter by lazy { SettingAdapter<AdapterItemModel>() }
    override fun onItemEvent(pos: Int) {
        when (pos) {
            0 -> showInputDialog()
            1 -> startActivity<StaffActivity>()
            3 -> startActivity<PreferenceActivity>()
            4 -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse("https//h5.m.taobao.com/awp/core/detail.htm?id=552270159252")
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
                startActivity(intent)
            }
            7 -> JFDialog.Builder(activity).setTitleText(getString(R.string.title)).setCancelText(getString(R.string.cancel)).setSureText(
                getString(R.string.sure)
            ).setDialogSureListener {
                startActivity(Intent().setAction(Intent.ACTION_CALL).setData(Uri.parse(getString(R.string.phonenum))))
            }.setContentText(getString(R.string.callphone)).create().show()
            8 -> startActivity<ChangePassActivity>()
        }

    }

    private fun showInputDialog() {
        val editText = EditText(activity)
        val inputDialog = AlertDialog.Builder(activity)
        inputDialog.setTitle("编辑公司名称").setView(editText)
        inputDialog.setNegativeButton("取消") { _, _ -> }
        inputDialog.setPositiveButton("确定") { _, _ ->
            val name = editText.text.toString()
            allItem[0].rightItem = name
            adapter.setDataEntityList(allItem)

            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "companyname" to name, "mobile" to loginModel?.mobile!!
                )
            )?.compose(NetworkScheduler.compose())
                ?.subscribe(object : ProgressSubscriber<BaseModel<String>>(activity!!) {
                    override fun onSuccess(data: BaseModel<String>?) {
                        toast(data?.msg!!)
                    }
                })
        }
        inputDialog.show()
    }

    override fun initView(): Int {
        return R.layout.fragment_data
    }

    override fun bindData() {
        tvTitle.text = "设置"
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.setdata)
        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }

        rcvData.layoutManager = LinearLayoutManager(activity)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(activity!!, RecyclerView.VERTICAL))
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

}

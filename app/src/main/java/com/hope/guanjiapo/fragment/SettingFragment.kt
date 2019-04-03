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
import com.hope.guanjiapo.activity.*
import com.hope.guanjiapo.adapter.SettingAdapter
import com.hope.guanjiapo.base.BaseFragment
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.AdapterItemModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.vehicleModel
import com.hope.guanjiapo.view.JFDialog
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.view_title.*
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
            2 -> startActivity<ConfigPrintActivity>()
            3 -> startActivity<PreferenceActivity>()
            4 -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse("https//h5.m.taobao.com/awp/core/detail.htm?id=552270159252")
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
                startActivity(intent)
            }
            5 -> startActivity<PayActivity>()
            6 -> //startActivity<ShareActivity>()
            {
                val textIntent = Intent(Intent.ACTION_SEND)
                textIntent.type = "text/plain"
                textIntent.putExtra(Intent.EXTRA_TEXT, "手机就能开单管帐，您专属的物流管理工具《物流管家婆》 http://wl.hfuture.cn")
                startActivity(Intent.createChooser(textIntent, "分享到"))
            }
            7 -> JFDialog.Builder(activity).setTitleText(getString(R.string.title)).setCancelText(getString(R.string.cancel)).setSureText(
                getString(R.string.sure)
            ).setDialogSureListener {
                startActivity(Intent().setAction(Intent.ACTION_CALL).setData(Uri.parse(getString(R.string.phonenum))))
            }.setContentText(getString(R.string.callphone)).create().show()
            8 -> startActivity<ChangePassActivity>()
            9 -> logout()
        }
    }

    private fun logout() {
        HttpNetUtils.getInstance().getManager()?.wllogout(
            hashMapOf(
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<String>>(activity) {
                override fun onSuccess(data: BaseModel<String>?) {
                    activity?.myApplicaton?.exitApp()
                    startActivity<LoginActivity>()
                    activity?.finish()
                }
            })
    }


    private fun showInputDialog() {
        val editText = EditText(activity)
        editText.setText(ApiUtils.vehicleModel?.companyname!!)
        val inputDialog = AlertDialog.Builder(activity)
        inputDialog.setTitle(R.string.editcompanyname).setView(editText)
        inputDialog.setNegativeButton(R.string.cancel) { _, _ -> }
        inputDialog.setPositiveButton(R.string.sure) { _, _ ->
            val name = editText.text.toString()
            allItem[0].rightItem = name
            adapter.setDataEntityList(allItem)

            HttpNetUtils.getInstance().getManager()?.editCompanyInfo(
                hashMapOf(
                    "companyname" to name,
                    "id" to loginModel?.id!!,
                    "clientCategory" to 4,
                    "clientVersion" to 1.0,
                    "mobile" to loginModel?.mobile!!,
                    "sessionId" to loginModel?.sessionid!!
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
        tvTitle.setText(R.string.tab_four)
        ivBackup.visibility = View.GONE

        val itemArr = resources.getStringArray(R.array.setdata)
        itemArr.forEach {
            val item = AdapterItemModel()
            item.items = it
            item.imgs = R.mipmap.ic_launcher
            allItem.add(item)
        }

        allItem.get(0).rightItem = vehicleModel?.companyname

        rcvData.layoutManager = LinearLayoutManager(activity)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(activity!!, RecyclerView.VERTICAL))
        adapter.setDataEntityList(allItem)
        adapter.itemListener = this
    }

}

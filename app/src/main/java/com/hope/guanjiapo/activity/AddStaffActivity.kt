package com.hope.guanjiapo.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.StaffModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.MD5Utils
import kotlinx.android.synthetic.main.activity_add_staff.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.toast

class AddStaffActivity : BaseActivity(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.radioButtonOne -> level = 1
            R.id.radioButtonTwo -> level = 2
            R.id.radioButtonThree -> level = 10
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> addstaff()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_add_staff
    }

    private var level: Int = 2
    private var change: Boolean = false
    override fun initData() {
        tvTitle.setText(R.string.addmember)
        tvTitleRight.setText(R.string.save)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        mSegmentedGroup.setOnCheckedChangeListener(this)

        change = intent.getBooleanExtra("change", false)
        val itemData: StaffModel? = intent.getSerializableExtra("item") as? StaffModel
        if (change) {
            etAccount.setText(itemData?.userName)
            etPhone.setText(itemData?.mobile)
            etPhone.isEnabled = false
        }
    }

    private fun addstaff() {
        val name = etAccount.text.toString()
        val phone = etPhone.text.toString()
        val pass = etPass.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast(R.string.error_name_empty)
            return
        }
        if (TextUtils.isEmpty(phone)) {
            toast(R.string.error_email_empty)
            return
        }
        if (TextUtils.isEmpty(pass)) {
            toast(R.string.error_password_empty)
            return
        }

        val passmd5 = MD5Utils.MD5Encode(pass, "utf-8")
        HttpNetUtils.getInstance().getManager()?.wladdOrDel(
            hashMapOf(
                "isAdd" to if (change) 2 else 1,
                "memberMobile" to phone,
                "memberName" to name,
                "id" to loginModel?.id!!,
                "memberPw" to passmd5,
                "memberSort" to 0,
                "memberType" to level,
                "mobile" to phone,
                "sessionId" to loginModel?.sessionid!!
            )
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<ArrayList<StaffModel>>>(this) {
                override fun onSuccess(data: BaseModel<ArrayList<StaffModel>>?) {
                    setResult(99, Intent().putExtra("data", data?.data!!))
                    finish()
                }
            })
    }
}

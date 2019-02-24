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
    override fun initData() {
        tvTitle.text = "添加员工"
        tvTitleRight.text = "保存"
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        mSegmentedGroup.setOnCheckedChangeListener(this)
    }

    private fun addstaff() {
        val name = etAccount.text.toString()
        val phone = etPhone.text.toString()
        val pass = etPass.text.toString()

        if (TextUtils.isEmpty(name)) {
            toast("名字不能为空")
            return
        }
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空")
            return
        }
        if (TextUtils.isEmpty(pass)) {
            toast("密码不能为空")
            return
        }

        val passmd5 = MD5Utils.MD5Encode(pass, "utf-8")
        HttpNetUtils.getInstance().getManager()?.wladdOrDel(
            hashMapOf(
                "isAdd" to 1,
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

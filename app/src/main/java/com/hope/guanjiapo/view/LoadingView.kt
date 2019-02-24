package com.hope.guanjiapo.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.hope.guanjiapo.R


/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 20 10:46
 * 类说明:
 */
class LoadingView : DialogFragment() {
    private var showDialog: Dialog? = null

    companion object {
        fun getInstance(msg: String? = "加载中", cancel: Boolean = false): LoadingView {
            val view = LoadingView()
            val bund = Bundle()
            bund.putString("msg", msg)
            bund.putBoolean("cancel", cancel)
            view.arguments = bund
            return view
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        showDialog = Dialog(activity, R.style.loadingDialogStyle)
        val view = LayoutInflater.from(activity).inflate(R.layout.view_dialog_loading, null)
        showDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        showDialog?.setContentView(view)

        val textview = view.findViewById<TextView>(R.id.tv)

        val dialogWindow = showDialog?.window
        dialogWindow?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val lp = dialogWindow?.attributes
        lp?.gravity = Gravity.CENTER
        lp?.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow?.attributes = lp

        val tempBund = arguments
        if (null != tempBund) {
            textview.text = tempBund.getString("msg")
            val isCancel = tempBund.getBoolean("cancel")
            showDialog?.setCanceledOnTouchOutside(isCancel)
            showDialog?.setCancelable(isCancel)
        }
        return showDialog!!
    }

    override fun onDestroy() {
        super.onDestroy()
        showDialog?.dismiss()
        showDialog = null
    }
}
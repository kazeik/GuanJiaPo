package com.hope.guanjiapo.net

import com.hope.guanjiapo.base.BaseActivity


/**
 * @author hope.chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 11 13 09:21
 * 类说明:
 */
abstract class ProgressSubscriber<T>(private val baseActivity: BaseActivity?, msg: String = "", cancel: Boolean = false) : SimpleSubscriber<T>(baseActivity) {

    constructor(baseActivity: BaseActivity, msg: String = "加载中..") : this(baseActivity, msg, false)

    init {
        baseActivity?.showDialog(msg, cancel)
    }

    override fun reLogin() {
    }

    override fun onError(e: Throwable) {
        super.onError(e)
        baseActivity?.hideDialog()
    }

    override fun onComplete() {
        super.onComplete()
        baseActivity?.hideDialog()
    }
}
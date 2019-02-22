package com.hope.guanjiapo.net

import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.utils.Utils.logs
import org.jetbrains.anko.toast


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 12 07 21:03
 * 类说明:
 */
abstract class SimpleSubscriber<T>(private val activity: BaseActivity?) : HttpSimpleSubscriber<T>() {

    override fun onComplete() {
    }

    override fun onError(errorCode: Int, msg: String?) {
        logs("tag", "错误消息 $msg")
        activity?.toast(msg!!)
    }
}
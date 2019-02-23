package com.hope.guanjiapo.net

import com.hope.guanjiapo.base.BaseModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * @author hope.chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 11 13 09:13
 * 类说明:
 */
abstract class HttpSimpleSubscriber<T> : Observer<T> {
    override fun onNext(data: T) {
        try {
            val model: BaseModel<T>? = data as? BaseModel<T>
            if (model == null) {
                onError(-999, "服务端返回的数据解析异常")
            } else {
                if (model.code == "error")
                    onError(-998, model.msg)
                else if (model.code == "success")
                    onSuccess(data)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun onError(e: Throwable) {
        var code = -1
        var errorMsg = e.message
        when (e) {
            is UnknownHostException -> {
                code = -400
                errorMsg = "服务器未找到"
            }
            is SocketTimeoutException -> {
                code = -404
                errorMsg = "服务器连接超时"
            }
            is ConnectException -> {
                code = -405
                errorMsg = "服务器连接异常"
            }
            is IllegalStateException -> {
                code = -405
                errorMsg = "服务器返回数据解析异常"
            }
        }
        onError(code, errorMsg)
        e.printStackTrace()
    }

    override fun onSubscribe(d: Disposable) {
    }

    abstract fun onSuccess(data: T?)
    abstract fun onError(errorCode: Int, msg: String?)
}
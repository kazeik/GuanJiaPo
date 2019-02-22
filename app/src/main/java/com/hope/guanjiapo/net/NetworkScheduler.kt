package com.hope.guanjiapo.net

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 11 28 14:40
 * 类说明:
 */
object NetworkScheduler {
    fun <T> compose(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}
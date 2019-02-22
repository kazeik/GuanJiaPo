package com.hope.guanjiapo.base


/**
 * @author hope.chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 11 12 19:30
 * 类说明:
 */
open class BaseModel<T> {
    var httpCode: Int = -1
    var error: String? = null
    var payload:T ?= null
}
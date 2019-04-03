package com.hope.guanjiapo.base


/**
 * @author hope.chen
 *         QQ:77132995 email:kazeik@163.com
 *         2018 11 12 19:30
 * 类说明:
 */
open class BaseModel<T> {
    var code: String? = ""
    var msg: String? = null
    var data: T? = null
    var sessionId: String? = null
}
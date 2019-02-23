package com.hope.guanjiapo.utils

import com.hope.guanjiapo.model.LoginModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 22 19:26
 * 类说明:
 */
object ApiUtils {

    var loginModel: LoginModel? = null

    const val baseUrl: String = "http://wl56.mmd520.cn/api/"

    const val login: String = "user/wllogin"

    const val regist: String = "user/wlreg"

    const val getmonthrevenue: String = "order/getmonthrevenue"

    const val getdayrevenue: String = "order/getdayrevenue"

    const val getConnector: String = "connector/getConnector"

    const val addoreditex:String = "connector/addoreditex"

    const val getcompanyPointList:String ="set/getcompanyPointList"

    const val addcompanyPoint:String ="set/addcompanyPoint"

    const val deletecompanyPoint:String = "set/deletecompanyPoint"
}
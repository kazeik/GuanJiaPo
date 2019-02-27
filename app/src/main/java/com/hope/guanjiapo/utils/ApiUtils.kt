package com.hope.guanjiapo.utils

import com.hope.guanjiapo.model.LoginModel
import com.hope.guanjiapo.model.StaffModel
import com.hope.guanjiapo.model.VehicleModel


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 22 19:26
 * 类说明:
 */
object ApiUtils {

    var loginModel: LoginModel? = null

    var vehicleModel: VehicleModel? = null
    var allStaffModel: List<StaffModel>? = null

    const val baseUrl: String = "http://wl56.mmd520.cn/api/"

    const val login: String = "user/wllogin"

    const val regist: String = "user/wlreg"

    const val getmonthrevenue: String = "order/getmonthrevenue"

    const val getdayrevenue: String = "order/getdayrevenue"

    const val getConnector: String = "connector/getConnector"

    const val addoreditex: String = "connector/addoreditex"

    const val getcompanyPointList: String = "set/getcompanyPointList"

    const val addcompanyPoint: String = "set/addcompanyPoint"

    const val deletecompanyPoint: String = "set/deletecompanyPoint"

    const val editcompanyPoint: String = "set/editcompanyPoint"

    const val wladdOrDel: String = "user/wladdOrDel"
    /**
     * 查询当前用户下的运单记录
     */
    const val wlget: String = "order/wlget"
    /**
     * 获取车次
     */
    const val getCompanyInfo: String = "set/getCompanyInfo"
    /**
     * 编辑信息
     */
    const val editCompanyInfo: String = "set/editCompanyInfo"
    const val wlreg: String = "user/wlreg"
    const val wllogout: String = "user/wllogout"
    const val connectordelete: String = "connector/delete"
    const val wxsearch: String = "orderwxpre/wxsearch"
    const val wxdelete: String = "orderwxpre/wxdelete"
    const val wladd: String = "order/wladd"
}
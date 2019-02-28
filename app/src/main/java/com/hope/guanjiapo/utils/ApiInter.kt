package com.hope.guanjiapo.utils

import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 22 19:27
 * 类说明:
 */
interface ApiInter {

    @POST(ApiUtils.login)
    @FormUrlEncoded
    fun login(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<LoginModel>>

    @POST(ApiUtils.regist)
    @FormUrlEncoded
    fun register(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<LoginModel>>

    @POST(ApiUtils.getmonthrevenue)
    @FormUrlEncoded
    fun getmonthrevenue(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<List<PerformanceListModel>>>

    @POST(ApiUtils.getdayrevenue)
    @FormUrlEncoded
    fun getdayrevenue(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<List<PerformanceListModel>>>

    @GET(ApiUtils.getConnector)
    fun getConnector(@QueryMap map: HashMap<String, Any>): Observable<BaseModel<List<ConsigneeModel>>>

    @POST(ApiUtils.addoreditex)
    @FormUrlEncoded
    fun addoreditex(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>


    @GET(ApiUtils.getcompanyPointList)
    fun getcompanyPointList(@QueryMap map: HashMap<String, Any>): Observable<BaseModel<List<DestinationModel>>>


    @POST(ApiUtils.addcompanyPoint)
    @FormUrlEncoded
    fun addcompanyPoint(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.deletecompanyPoint)
    @FormUrlEncoded
    fun deletecompanyPoint(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.wladdOrDel)
    @FormUrlEncoded
    fun wladdOrDel(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<ArrayList<StaffModel>>>


    @POST(ApiUtils.wlget)
    @FormUrlEncoded
    fun wlget(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<List<WaybillModel>>>

    @POST(ApiUtils.editCompanyInfo)
    @FormUrlEncoded
    fun editCompanyInfo(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @GET(ApiUtils.getCompanyInfo)
    fun getCompanyInfo(@QueryMap map: HashMap<String, Any>): Observable<BaseModel<VehicleModel>>

    @POST(ApiUtils.wlreg)
    @FormUrlEncoded
    fun wlreg(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.wllogout)
    @FormUrlEncoded
    fun wllogout(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.editcompanyPoint)
    @FormUrlEncoded
    fun editcompanyPoint(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.connectordelete)
    @FormUrlEncoded
    fun connectordelete(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.wxdelete)
    @FormUrlEncoded
    fun wxdelete(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.wladd)
    @FormUrlEncoded
    fun wladd(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<String>>

    @POST(ApiUtils.wxsearch)
    @FormUrlEncoded
    fun wxsearch(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<List<SubscribeModel>>>

    @POST(ApiUtils.wlsearch)
    @FormUrlEncoded
    fun wlsearch(@FieldMap map: HashMap<String, Any>): Observable<BaseModel<List<WaybillModel>>>
}
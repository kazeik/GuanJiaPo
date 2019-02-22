package com.hope.guanjiapo.utils

import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.LoginModel
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 22 19:27
 * 类说明:
 */
interface ApiInter {

    @POST(ApiUtils.login)
    @FormUrlEncoded
    fun login(@FieldMap map: HashMap<String,Any>): Observable<BaseModel<LoginModel>>

    @POST(ApiUtils.regist)
    @FormUrlEncoded
    fun register(@FieldMap map: HashMap<String,Any>): Observable<BaseModel<LoginModel>>
}
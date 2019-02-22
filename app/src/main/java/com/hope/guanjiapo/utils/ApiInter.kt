package com.hope.guanjiapo.utils

import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.LoginModel
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 02 22 19:27
 * 类说明:
 */
interface ApiInter {

    @POST(ApiUtils.login)
    fun login(@Body body: RequestBody): Observable<BaseModel<LoginModel>>

    @POST(ApiUtils.regist)
    fun register(@Body body: RequestBody): Observable<BaseModel<LoginModel>>
}
@file:Suppress("DEPRECATION")

package com.hope.guanjiapo.net

import com.google.gson.Gson
import com.hope.guanjiapo.BuildConfig
import com.hope.guanjiapo.utils.ApiInter
import com.hope.guanjiapo.utils.ApiUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2017 04 27 11:28
 * 类说明:
 */
class HttpNetUtils {
    private val okClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(time, TimeUnit.SECONDS)
            .readTimeout(time, TimeUnit.SECONDS)
            .writeTimeout(time, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.LOG_DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
            )
            .build()
    }
    private val time: Long = 15
    private val retrofit: Retrofit? by lazy {
        Retrofit.Builder()
            .baseUrl(ApiUtils.baseUrl)
            .client(okClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


//
//    fun getUploadImageParts(resultList: List<ImageBean>): List<MultipartBody.Part> {
//        val requetBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
//        for (item in resultList) {
//            val file = File(item.imagePath)
//            val imgbody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//            requetBuilder.addFormDataPart("file", file.name, imgbody)
//        }
//        return requetBuilder.build().parts()
//    }


    fun getParamsBody(dataMap: HashMap<String, Any>): RequestBody {
        val postInfoStr = Gson().toJson(dataMap)
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfoStr)
    }

    fun getManager(): ApiInter? {
        return retrofit?.create(ApiInter::class.java)
    }

    companion object {
        fun getInstance(): HttpNetUtils {
            synchronized(HttpNetUtils::class.java) {
                return HttpNetUtils()
            }
        }
    }
}

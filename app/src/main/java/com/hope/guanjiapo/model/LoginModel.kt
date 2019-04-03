package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("bossMobile")
    val bossMobile: String,
    @SerializedName("buyYear")
    val buyYear: String,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("invitedmobile")
    val invitedmobile: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("overTime")
    val overTime: Long,
    @SerializedName("payTime")
    val payTime: Long,
    @SerializedName("sort")
    val sort: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("userLevel")
    val userLevel: Int,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userPwd")
    val userPwd: String,
    @SerializedName("userType")
    val userType: Int
//    @SerializedName("sessionid")
//    var sessionid: String
)
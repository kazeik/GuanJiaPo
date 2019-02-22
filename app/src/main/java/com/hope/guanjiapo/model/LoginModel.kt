package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("bossMobile")
    val bossMobile: String,
    @SerializedName("buyYear")
    val buyYear: Int,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("invitedmobile")
    val invitedmobile: String,
    @SerializedName("mobile")
    val mobile: Long,
    @SerializedName("overTime")
    val overTime: Long,
    @SerializedName("payTime")
    val payTime: Long,
    @SerializedName("sessionid")
    val sessionid: Long,
    @SerializedName("sort")
    val sort: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("userLevel")
    val userLevel: Int,
    @SerializedName("userName")
    val userName: Long,
    @SerializedName("userPwd")
    val userPwd: String,
    @SerializedName("userType")
    val userType: Int
)
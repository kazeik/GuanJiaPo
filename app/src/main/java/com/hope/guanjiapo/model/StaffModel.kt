package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StaffModel(
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("bossMobile")
    val bossMobile: String,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("overTime")
    val overTime: Long,
    @SerializedName("sessionid")
    val sessionid: String,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("userLevel")
    val userLevel: Int,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userType")
    val userType: Int
):Serializable
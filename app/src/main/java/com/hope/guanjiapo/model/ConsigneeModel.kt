package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConsigneeModel(
    @SerializedName("addr")
    val addr: String,
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("point")
    val point: Int,
    @SerializedName("remain")
    val remain: Int,
    @SerializedName("sort")
    val sort: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("type")
    val type: Int
):Serializable
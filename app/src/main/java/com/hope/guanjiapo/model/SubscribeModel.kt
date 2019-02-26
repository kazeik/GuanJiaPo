package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class SubscribeModel(
    @SerializedName("agentmoney")
    val agentmoney: Int,
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("clientid")
    val clientid: Int,
    @SerializedName("clientname")
    val clientname: String,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("id")
    val id: Int,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("productcount")
    val productcount: Int,
    @SerializedName("productdescript")
    val productdescript: String,
    @SerializedName("receiveraddress")
    val receiveraddress: String,
    @SerializedName("receivername")
    val receivername: String,
    @SerializedName("receiverphone")
    val receiverphone: String,
    @SerializedName("recno")
    val recno: Int,
    @SerializedName("senderaddress1")
    val senderaddress1: String,
    @SerializedName("sendername")
    val sendername: String,
    @SerializedName("senderphone")
    val senderphone: String,
    @SerializedName("shipfeestate")
    val shipfeestate: Int,
    @SerializedName("status")
    val status: Int
)
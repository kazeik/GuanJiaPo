package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubscribeModel(
    @SerializedName("agentmoney")
    val agentmoney: String,
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("clientid")
    val clientid: Int,
    @SerializedName("clientname")
    val clientname: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("copycount")
    val copycount: Int,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("insurance")
    val insurance: Int,
    @SerializedName("insurancefee")
    val insurancefee: String,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("productcount")
    val productcount: String,
    @SerializedName("productdescript")
    val productdescript: String,
    @SerializedName("productsize")
    val productsize: String,
    @SerializedName("productweight")
    val productweight: String,
    @SerializedName("receiveraddress")
    val receiveraddress: String,
    @SerializedName("receivername")
    val receivername: String,
    @SerializedName("receiverphone")
    val receiverphone: String,
    @SerializedName("recno")
    val recno: Int,
    @SerializedName("recway")
    val recway: Int,
    @SerializedName("senderaddress1")
    val senderaddress1: String,
    @SerializedName("sendername")
    val sendername: String,
    @SerializedName("senderphone")
    val senderphone: String,
    @SerializedName("shipfeepaytype")
    val shipfeepaytype: Int,
    @SerializedName("shipfeestate")
    val shipfeestate: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("updatorMobile")
    val updatorMobile: String,
    @SerializedName("waitnotify")
    val waitnotify: Int
) : Serializable
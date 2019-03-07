package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WaybillModel(
    @SerializedName("agentmoney")
    val agentmoney: String,
    @SerializedName("baseshipfee")
    val baseshipfee: String,
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("bossMobile")
    val bossMobile: String,
    @SerializedName("carname")
    val carname: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("copycount")
    val copycount: String,
    @SerializedName("costFee")
    val costFee: String,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("dispatchfee")
    val dispatchfee: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("insurancefee")
    val insurancefee: String,
    @SerializedName("oderstate")
    var oderstate: Int,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("productcount")
    val productcount: Double,
    @SerializedName("productdescript")
    val productdescript: String,
    @SerializedName("productno")
    val productno: String,
    @SerializedName("productsize")
    val productsize: String,
    @SerializedName("productweight")
    val productweight: String,
    @SerializedName("receivepoint")
    val receivepoint: String,
    @SerializedName("receiveraddress")
    val receiveraddress: String,
    @SerializedName("receivername")
    val receivername: String,
    @SerializedName("receiverphone")
    val receiverphone: String,
    @SerializedName("recno")
    val recno: String,
    @SerializedName("recway")
    val recway: Int,
    @SerializedName("returnmoney")
    val returnmoney: String,
    @SerializedName("senderaddress")
    val senderaddress: String,
    @SerializedName("sendername")
    val sendername: String,
    @SerializedName("senderphone")
    val senderphone: String,
    @SerializedName("serviceName")
    val serviceName: String,
    @SerializedName("shipfee")
    val shipfee: String,
    @SerializedName("shipfeepaytype")
    val shipfeepaytype: Int,
    @SerializedName("shipfeesendpay")
    val shipfeesendpay: String,
    @SerializedName("shipfeestate")
    val shipfeestate: String,
    @SerializedName("sort")
    val sort: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("updatorMobile")
    val updatorMobile: String,
    @SerializedName("waitnotify")
    val waitnotify: String
) : Serializable
package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class WaybillModel(
    @SerializedName("agentmoney")
    val agentmoney: Int,
    @SerializedName("baseshipfee")
    val baseshipfee: Int,
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("bossMobile")
    val bossMobile: String,
    @SerializedName("carname")
    val carname: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("copycount")
    val copycount: Int,
    @SerializedName("costFee")
    val costFee: Int,
    @SerializedName("createDate")
    val createDate: Long,
    @SerializedName("dispatchfee")
    val dispatchfee: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("insurancefee")
    val insurancefee: Int,
    @SerializedName("oderstate")
    val oderstate: Int,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("productcount")
    val productcount: Int,
    @SerializedName("productdescript")
    val productdescript: String,
    @SerializedName("productno")
    val productno: String,
    @SerializedName("productsize")
    val productsize: Int,
    @SerializedName("productweight")
    val productweight: Int,
    @SerializedName("receivepoint")
    val receivepoint: String,
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
    @SerializedName("returnmoney")
    val returnmoney: Int,
    @SerializedName("senderaddress")
    val senderaddress: String,
    @SerializedName("sendername")
    val sendername: String,
    @SerializedName("senderphone")
    val senderphone: String,
    @SerializedName("serviceName")
    val serviceName: String,
    @SerializedName("shipfee")
    val shipfee: Int,
    @SerializedName("shipfeepaytype")
    val shipfeepaytype: Int,
    @SerializedName("shipfeesendpay")
    val shipfeesendpay: Int,
    @SerializedName("shipfeestate")
    val shipfeestate: Int,
    @SerializedName("sort")
    val sort: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("updateDate")
    val updateDate: Long,
    @SerializedName("updatorMobile")
    val updatorMobile: String,
    @SerializedName("waitnotify")
    val waitnotify: Int
)
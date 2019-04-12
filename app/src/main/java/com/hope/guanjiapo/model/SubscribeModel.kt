package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubscribeModel(
    @SerializedName("clientid")
    val clientid: Int,
    @SerializedName("clientname")
    val clientname: String,
    @SerializedName("transferName")
    val transferName: String,
    @SerializedName("insurance")
    val insurance: Int,
    @SerializedName("senderaddress1")
    val senderaddress1: String
) : Serializable, WaybillModel()
package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class SaleorderrecordModel(
    @SerializedName("agentmoney")
    val agentmoney: Double,
    @SerializedName("costFee")
    val costFee: Double,
    @SerializedName("dispatchfee")
    val dispatchfee: Double,
    @SerializedName("insurancefee")
    val insurancefee: Double,
    @SerializedName("shipfee")
    val shipfee: Double,
    @SerializedName("shipfeesendpay")
    val shipfeesendpay: Double
)
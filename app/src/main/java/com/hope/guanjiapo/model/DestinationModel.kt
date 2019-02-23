package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class DestinationModel(
    @SerializedName("bossId")
    val bossId: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("operatorMobile")
    val operatorMobile: String,
    @SerializedName("receivepoint")
    val receivepoint: String,
    @SerializedName("type")
    val type: Int
)
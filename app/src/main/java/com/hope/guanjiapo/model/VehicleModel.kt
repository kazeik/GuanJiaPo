package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class VehicleModel(
    @SerializedName("bossid")
    val bossid: String,
    @SerializedName("companyname")
    val companyname: String,
    @SerializedName("fahuodilist")
    val fahuodilist: String,
    @SerializedName("reccarnolist")
    val reccarnolist: String,
    @SerializedName("recpointlist")
    val recpointlist: String,
    @SerializedName("servicenamelist")
    val servicenamelist: String,
    @SerializedName("sort")
    val sort: Int,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("wrapnamelist")
    val wrapnamelist: String
)
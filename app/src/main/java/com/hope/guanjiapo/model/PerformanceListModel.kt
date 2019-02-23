package com.hope.guanjiapo.model

import com.google.gson.annotations.SerializedName

data class PerformanceListModel(
    @SerializedName("date")
    val date: String,
    @SerializedName("saleorderrecord")
    val saleorderrecord: SaleorderrecordModel
)
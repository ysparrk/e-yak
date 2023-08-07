package com.a103.eyakrev1

import com.google.gson.annotations.SerializedName

data class Medicine (
    val id: Int = -1,
    val icd: String = "",
    val customName: String = "",
    val krName: String = "",
    val engName: String = "",
    val startDateTime: String = "",
    val endDateTime: String = "",
    @SerializedName("medicineRoutines")
    val routines: ArrayList<String> = arrayListOf<String>("",),
    val iotLocation: Int = -1,
    val medicineShape: Int = -1,
    val medicineDose : Float = 0.0F,
    val unit: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)
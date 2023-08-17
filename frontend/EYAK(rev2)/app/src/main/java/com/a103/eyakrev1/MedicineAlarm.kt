package com.a103.eyakrev1

class MedicineRoutines (
    val bedAfterQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val breakfastBeforeQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val breakfastAfterQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val lunchBeforeQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val lunchAfterQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val dinnerBeforeQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val dinnerAfterQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
    val bedBeforeQueryResponses: ArrayList<medicineInRoutine> = arrayListOf(),
)

data class medicineInRoutine (
    val id: Int,
    val customName: String,
    val iotLocation: Int,
    val medicineShape: Int,
    val took: Boolean,
)
package com.a103.eyakrev1

data class memberDto(
    var id: Int,
    var providerName: String,
    var refreshToken: String,
    var nickname: String,
    var createdAt: String,
    var updatedAt: String,
    var wakeTime: String,
    var breakfastTime: String,
    var lunchTime: String,
    var dinnerTime: String,
    var bedTime: String,
    var eatingDuration: String
)

data class SignUpBodyModel(
    var providerName: String,
    var token: String?,
    var nickname: String?,
    var wakeTime: String,
    var breakfastTime: String,
    var lunchTime: String,
    var dinnerTime: String,
    var bedTime: String,
    var eatingDuration: String
)

data class LoginResponseModel(
    var accessToken: String,
    var refreshToken: String,
    var memberDto: memberDto
)

data class LoginBodyModel(
    var providerName: String,
    var token: String
)

data class ChangeAccountInfoBodyModel(
    var wakeTime: String,
    var breakfastTime: String,
    var lunchTime: String,
    var dinnerTime: String,
    var bedTime: String,
    var eatingDuration: String,
)

data class ChangeAccountInfoResponseModel(
    var id: Int,
    var providerName: String,
    var refreshToken: String,
    var nickname: String,
    var createdAt: String,
    var updatedAt: String,
    var wakeTime: String,
    var breakfastTime: String,
    var lunchTime: String,
    var dinnerTime: String,
    var bedTime: String,
    var eatingDuration: String,
    var oauthId: String,
)

data class FollowRequestBodyModel(
    var followerScope: String,
    var followeeNickname: String,
    var customName: String,
)

data class PrescriptionBodyModel(   // 복약 정보 등록 바디
    var icd: String,
    var kr_name: String,
    var eng_name: String,
    var custom_name: String,
    var start_date_time: String,
    var end_date_time: String,
    var medicine_routines: MutableList<String>,
    var medicine_shape: Int,
    var medicine_dose: Float,
    var unit: String,
)

data class PrescriptionEditBodyModel(
    var icd: String,
    var krName: String,
    var engName: String,
    var customName: String,
    var startDateTime: String,
    var endDateTime: String,
    var medicineRoutines: MutableList<String>,
    var iotLocation: Int,
    var medicineShape: Int,
    var medicineDose: Float,
    var unit: String,
)

data class Dates(
    var date: String,
    var full_dose: Int,
    var actual_dose: Int,
)

data class FollowRequestsDataModel(
    var followRequestId: Int,
    var followerId: Int,
    var followeeId: Int,
    var followerNickname: String,
    var followeeNickname: String,
    var customName: String,
    var scope: String,
)

data class AcceptFollowRequestBodyModel(
    var followeeScope: String,
    var customName: String,
)

data class TodayDoseInfoBodyModel(
    var date: String,
    var fullDose: Int,
    var actualDose: Int,
)

data class DailySurveyContentsBodyModel(
    var surveyContentId: Int,
    var surveyContentType: String,
)
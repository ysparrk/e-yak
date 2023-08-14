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

data class medicineDetailsInCalendarResponseModel(
    var date: String,
    var medicineRoutineDateDtos: ArrayList<MedicineDetailInCalendar>,
    var surveyContentDtos: surveyContentDtos,
)

data class MedicineDetailInCalendar(
    var date: String?,
    var prescriptionId: Int,
    var customName: String,
    var routine: String,
    var took: Boolean,
)

data class surveyContentDtos(
    var contentEmotionResultResponse: contentEmotionResultResponse,
    var contentStatusResultResponse: contentStatusResultResponse,
    var contentTextResultResponse: contentTextResultResponse,
)

data class contentEmotionResultResponse(
    var contentEmotionResultId: Int,
    var memberId: Int,
    var choiceEmotion: String,
    var createdAt: String,
    var updatedAt: String
)

data class contentStatusResultResponse(
    var contentStatusResultId: Int,
    var memberId: Int,
    var selectedStatusChoices: ArrayList<String>,
    var createdAt: String,
    var updatedAt: String
)

data class contentTextResultResponse(
    var contentTextResultId: Int,
    var memberId: Int,
    var text: String,
    var createdAt: String,
    var updatedAt: String
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
    var fullDose: Int,
    var actualDose: Int,
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
    var surveyContentId: Long,
    var surveyContentType: String,
)

data class getMedicineRoutineCheckIdBodyModel(
    var date: String,
    var routine: String,
    var prescriptionId: Int,
)

data class getMedicineRoutineCheckIdResponseBody(
    var id: Int,
    var took: Boolean,
)

data class medicineRoutineCheckBodyModel(
    val id: Int,
    val date: String,
    val routine: String,
    val took: Boolean,
    val memberId: Int,
    val prescriptionId: Int,
)

data class DailySurveyResultBodyModel(
    var contentTextResultResponse: textResultModel,
    var contentStatusResultResponse: statusResultModel,
    var contentEmotionResultResponse: emotionResultModel,
)

data class textResultModel(
    var contentTextResultId: Long,
    var memberId: Long,
    var text: String,
    var createdAt: String,
    var updatedAt: String,
)
data class statusResultModel(
    var contentStatusResultId: Long,
    var memberId: Long,
    var selectedStatusChoices: ArrayList<String>,
    var createdAt: String,
    var updatedAt: String,
)

data class emotionResultModel(
    var contentEmotionResultId: Long,
    var memberId: Long,
    var choiceEmotion: String,
    var createdAt: String,
    var updatedAt: String,
)

data class ContentTextResultsBodyModel(
    var text: String,
)

data class EditContentTextResultsBodyModel(
    var contentTextResultId: Long,
    var text: String,
)

data class ContentEmotionResultsBodyModel(
    var choiceEmotion: String,
)

data class EditContentEmotionResults(
    var contentEmotionResultId: Long,
    var choiceEmotion: String,
)

data class ContentStatusResultsBodyModel(
    val selectedStatusChoices: ArrayList<String>,
)

data class EditContentStatusBodyModel(
    var contentStatusResultId: Long,
    var selectedStatusChoices: ArrayList<String>,
)

data class MedicineSearchResponseBodyModel(
    var prescriptionList: ArrayList<PrescriptionListModel>,
    var surveyContentList : ArrayList<SurveyContentListModel>,
)

data class PrescriptionListModel(
    var customName: String,
    var icd: Long,
    var krName: String,
    var engName: String,
    var startDateTime: String,
    var endDateTime: String,
    var iotLocation: Int,
    var medicineShape: Int,
    var medicineDose: Float,
    var unit: String,
    var medicineRoutines: ArrayList<String>,
    var fullDose: Int,
    var actualDose: Int,
)

data class SurveyContentListModel(
    var date: String,
    var contentEmotionResultResponse: emotionResultModel,
    var contentStatusResultResponse: statusResultModel,
    var contentTextResultResponse: textResultModel,
)
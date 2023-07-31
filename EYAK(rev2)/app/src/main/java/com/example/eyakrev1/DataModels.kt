package com.example.eyakrev1

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

data class LoginResponseModel(
    var accessToken: String,
    var refreshToken: String,
    var memberDto: memberDto
)

data class LoginBodyModel(
    var providerName: String,
    var token: String
)
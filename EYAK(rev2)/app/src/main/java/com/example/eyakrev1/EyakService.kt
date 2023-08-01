package com.example.eyakrev1

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit

interface EyakService {

    // https://stickode.tistory.com/43
    @POST("/api/v1/auth/signup")
    fun signUp(
        @Body params: SignUpBodyModel,
    ): Call<Void>

    @POST("/api/v1/auth/signin")
    fun signIn(
        @Body params: LoginBodyModel,
    ): Call<LoginResponseModel>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://i9a103.p.ssafy.io" // BASE 주소

        fun create(): EyakService {
            val gson :Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(EyakService::class.java)
        }
    }
}
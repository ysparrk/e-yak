package com.a103.eyakrev1

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/api/v1/auth/duplication")
    fun checkDuplicate(
        @Query("nickname") nickname: String,
    ): Call<Boolean>

    @DELETE("/api/v1/members/{memberId}")
    fun deleteAccount(
        @Path("memberId") memberId: Int,
        @Header("Authorization") Authorization: String,
    ): Call<Void>

    @PUT("/api/v1/members/{memberId}")
    fun changeAccountInfo(
        @Path("memberId") memberId: Int,
        @Body params: ChangeAccountInfoBodyModel,
        @Header("Authorization") Authorization: String,
    ): Call<ChangeAccountInfoResponseModel>

    @POST("/api/v1/members/{followerId}/follow-requests")
    fun followRequest(
        @Path("followerId") followerId: Int,
        @Header("Authorization") Authorization: String,
        @Body params: FollowRequestBodyModel,
    ): Call<Void>

    @POST("/api/v1/prescriptions")  // 복약 정보 등록
    fun prescription(
        @Header("Authorization") Authorization: String,
        @Body params: PrescriptionBodyModel,
    ): Call<Void>
    
    @GET("/api/v1/follow-requests") // 사용자가 요청한/사용자에게 요청된 팔로우 요청 전체 조회
    fun followRequests(
        @Header("Authorization") Authorization: String,
        @Query("isGetFollowers") isGetFollowers: Boolean,
    ): Call<MutableList<FollowRequestsDataModel>>

    @POST("/api/v1/members/{followerId}/follow-requests/{followRequestId}")
    fun acceptFollowRequest(
        @Path("followerId") followerId: Int,
        @Path("followRequestId") followRequestId: Int,
        @Header("Authorization") Authorization: String,
        @Body params: AcceptFollowRequestBodyModel,
    ): Call<Void>
    @GET("/api/v1/prescriptions")
    fun getAllPrescriptions(
        @Header("Authorization") Authorization: String,
    ): Call<ArrayList<Medicine>>

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
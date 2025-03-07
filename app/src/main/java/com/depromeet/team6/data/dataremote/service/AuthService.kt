package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.RequestSignUpDto
import com.depromeet.team6.data.dataremote.model.response.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.ResponseAuthDto
import com.depromeet.team6.data.dataremote.model.response.ResponseCheckDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.AUTH
import com.depromeet.team6.data.dataremote.util.ApiConstraints.CHECK
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOGIN
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOGOUT
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @GET("$API/$AUTH/$CHECK")
    suspend fun getCheck(
        @Query("provider") provider: Int
    ): ApiResponse<ResponseCheckDto>

    @POST("$API/$AUTH/$LOGIN")
    suspend fun postSignUp(
        @Body requestSignUpDto: RequestSignUpDto
    ): ApiResponse<ResponseAuthDto>

    @GET("$API/$AUTH/$LOGIN")
    suspend fun getLogin(
        @Query("provider") provider: Int
    ): ApiResponse<ResponseAuthDto>

    @GET("$API/$AUTH/$LOGOUT")
    suspend fun getLogout(): ApiResponse<Unit>
}

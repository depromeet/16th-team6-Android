package com.depromeet.team6.data.dataremote.service

import com.depromeet.team6.data.dataremote.model.request.signup.RequestSignUpDto
import com.depromeet.team6.data.dataremote.model.response.base.ApiResponse
import com.depromeet.team6.data.dataremote.model.response.user.ResponseAuthDto
import com.depromeet.team6.data.dataremote.model.response.user.ResponseCheckDto
import com.depromeet.team6.data.dataremote.util.ApiConstraints.API
import com.depromeet.team6.data.dataremote.util.ApiConstraints.AUTH
import com.depromeet.team6.data.dataremote.util.ApiConstraints.CHECK
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOGIN
import com.depromeet.team6.data.dataremote.util.ApiConstraints.LOGOUT
import com.depromeet.team6.data.dataremote.util.ApiConstraints.ME
import com.depromeet.team6.data.dataremote.util.ApiConstraints.MEMBERS
import com.depromeet.team6.data.dataremote.util.ApiConstraints.PROVIDER
import com.depromeet.team6.data.dataremote.util.ApiConstraints.SIGNUP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @GET("$API/$AUTH/$CHECK")
    suspend fun getCheck(
        @Query(PROVIDER) provider: Int
    ): ApiResponse<ResponseCheckDto>

    @POST("$API/$AUTH/$SIGNUP")
    suspend fun postSignUp(
        @Body requestSignUpDto: RequestSignUpDto
    ): ApiResponse<ResponseAuthDto>

    @GET("$API/$AUTH/$LOGIN")
    suspend fun getLogin(
        @Query(PROVIDER) provider: Int
    ): ApiResponse<ResponseAuthDto>

    @POST("$API/$AUTH/$LOGOUT")
    suspend fun postLogout(): ApiResponse<Unit>

    @DELETE("$API/$MEMBERS/$ME")
    suspend fun deleteWithDraw(): Response<Unit>
}

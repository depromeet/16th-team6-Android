package com.depromeet.team6.data.dataremote.model.response.base

import androidx.annotation.Keep
import com.depromeet.team6.data.dataremote.model.request.exeption.RequestException
import com.google.gson.Gson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response

@Keep
@Serializable
data class ApiResponse<T>(
    @SerialName("responseCode") val responseCode: String,
    @SerialName("timeStamp") val timeStamp: String? = null,
    @SerialName("result") val result: T? = null,
    @SerialName("message") val message: String? = null
)

fun <T> ApiResponse<T>.toResult(): Result<T> =
    when {
        this.result != null -> Result.success(this.result)
        this.message != null -> Result.failure(RequestException(this.responseCode, this.message))
        else -> Result.failure(Exception("Unknown error occurred"))
    }

suspend fun <T> ApiResponse.Companion.handle(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Result<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            response.body()?.toResult()
                ?: Result.failure(IllegalStateException("response is null"))
        } else {
            when (response.code()) {
                in 400..499 -> {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = try {
                        Gson().fromJson(errorBody, ApiResponse::class.java)
                    } catch (e: Exception) {
                        return Result.failure(IllegalStateException("Error parsing response"))
                    }

                    Result.failure(
                        RequestException(
                            errorResponse.responseCode ?: "UNKNOWN",
                            errorResponse.message ?: "Unknown error"
                        )
                    )
                }
                else -> Result.failure(IllegalStateException("서버로부터 응답이 없습니다."))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
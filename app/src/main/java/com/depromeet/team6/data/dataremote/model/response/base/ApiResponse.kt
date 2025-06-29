package com.depromeet.team6.data.dataremote.model.response.base

import androidx.annotation.Keep
import com.depromeet.team6.presentation.util.ErrorToastMessage.MSG_UNKNOWN_NETWORK_ERROR
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response

typealias BaseResponse<T> = Response<ApiResponse<T>>

@Keep
@Serializable
data class ApiResponse<T>(
    @SerialName("responseCode") val responseCode: String,
    @SerialName("timeStamp") val timeStamp: String? = null,
    @SerialName("result") val result: T? = null,
    @SerialName("message") val message: String? = null
)

@Deprecated("이 메서드 대신 Response.parse() 확장함수 사용하도록 전부 리팩토링 해주세요")
fun <T> ApiResponse<T>.toResult(): Result<T> =
    when {
        this.result != null -> Result.success(this.result)
        this.message != null -> Result.failure(ApiException.NetworkFailureException(MSG_UNKNOWN_NETWORK_ERROR))
        else -> Result.failure(Exception("Unknown error occurred"))
    }

@Suppress("UNCHECKED_CAST")
suspend fun <T : Any, Z : ApiResponse<T>> Response<Z>.parse(): Result<T> {
    return if (isSuccessful) {
        val apiBody = body()
            ?: return Result.success(Unit as T)

        if (apiBody.result == null) {
            return Result.success(Unit as T)
        }

        return Result.success(apiBody.result)
    } else {
        errorBody()?.charStream()?.use { reader ->
            withContext(Dispatchers.IO) {
                val errorBodyString = reader.readText()
                val errorBody = try {
                    // errorBody를 ApiResponse로 파싱하는게 맞나? (성공 실패 body 형식 확인해봐야 함)
                    Gson().fromJson(errorBodyString, ApiErrorResponse::class.java)
                } catch (e: Exception) {
                    return@withContext Result.failure(
                        ApiException.NetworkFailureException(
                            errorMessage = e.message ?: "ErrorResponse Parsing Error"
                        )
                    )
                }
                val apiException = ApiException.ApiRequestFailureException(
                    errorBody.responseCode,
                    errorBody.message ?: "Unknown error"
                )
                return@withContext Result.failure(apiException)
            }
        }
            ?: return Result.failure(ApiException.NetworkFailureException())
    }
}

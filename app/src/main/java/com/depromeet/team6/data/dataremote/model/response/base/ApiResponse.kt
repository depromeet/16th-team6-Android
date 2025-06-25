package com.depromeet.team6.data.dataremote.model.response.base

import androidx.annotation.Keep
import com.depromeet.team6.presentation.util.ErrorToastMessage.MSG_UNKNOWN_NETWORK_ERROR
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import timber.log.Timber

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

suspend fun <T : Any, Z : ApiResponse<T>> Response<Z>.parse(): Result<T> {
    return if (isSuccessful) {
        val apiBody = body()
            ?: return Result.failure(
                ApiException.ApiRequestFailureException("EMPTY", "Response Body is Empty")
            )

        if (apiBody.result == null) {
            return Result.failure(ApiException.NetworkFailureException(errorMessage = "Response Result is Empty"))
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
                        return@withContext Result.failure(ApiException.NetworkFailureException(errorMessage = e.message ?: "ErrorResponse Parsing Error"))
                    }
                val apiException = ApiException.ApiRequestFailureException(errorBody.responseCode, errorBody.message ?: "Unknown error")
                Timber.d("test test ya ya ya : ${apiException}")
                return@withContext Result.failure(apiException)
            }
        }
            ?: return Result.failure(ApiException.NetworkFailureException())
    }
}

//suspend fun <T> ApiResponse.Companion.parse(
//    apiCall: suspend () -> Response<ApiResponse<T>>
//): Result<T> {
//    return try {
//        val response = apiCall()
//
//        if (response.isSuccessful) {
//            // success인데 body가 null인 경우가 있나?
//            response.body()?.toResult()
//                ?: Result.failure(IllegalStateException("response is null"))
//        } else {
//            val errorBody = response.errorBody()?.string()
//            val errorResponse =
//                try {
//                    // errorBody를 ApiResponse로 파싱하는게 맞나? (성공 실패 body 형식 확인해봐야 함)
//                    Gson().fromJson(errorBody, ApiResponse::class.java)
//                } catch (e: Exception) {
//                    return Result.failure(IllegalStateException("Error parsing response"))
//                }
//
//            Result.failure(
//                ApiException.ShowToastException(MSG_UNKNOWN_NETWORK_ERROR)
//            )
//            when (response.code()) {
//                in 400..499 -> {
//                    val errorBody = response.errorBody()?.string()
//                    val errorResponse = try {
//                        Gson().fromJson(errorBody, ApiResponse::class.java)
//                    } catch (e: Exception) {
//                        return Result.failure(IllegalStateException("Error parsing response"))
//                    }
//
//                    Result.failure(
//                        RequestException(
//                            errorResponse.responseCode ?: "UNKNOWN",
//                            errorResponse.message ?: "Unknown error"
//                        )
//                    )
//                }
//                else -> Result.failure(IllegalStateException("서버로부터 응답이 없습니다."))
//            }
//        }
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//}
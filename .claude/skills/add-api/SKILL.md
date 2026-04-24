---
name: add-api
description: Atcha Android 프로젝트에서 API 스펙(HTTP method, URL, params, response)을 입력받아 Service → DataSource → Repository → UseCase 전 레이어를 자동으로 구현한다. 사용자가 API sheet나 API 명세를 보여주며 구현을 요청할 때 사용한다.
---

# Add API Layer

API 스펙을 기반으로 Android Clean Architecture 전 레이어를 자동 구현한다.

## 프로젝트 구조 (고정)

```
Base package : com.depromeet.team6
Service      : data/dataremote/service/
DataSource   : data/dataremote/datasource/
Response DTO : data/dataremote/model/response/<domain>/
Request DTO  : data/dataremote/model/request/<domain>/
Mapper       : data/mapper/todomain/
Repository   : domain/repository/
RepoImpl     : data/repositoryimpl/
UseCase      : domain/usecase/
Constraints  : data/dataremote/util/Constraints.kt
```

## 핵심 원칙: Response DTO 생성 기준

서버 응답은 항상 아래 wrapper로 감싸인다:

```kotlin
// BaseResponse<T> = Response<ApiResponse<T>>
data class ApiResponse<T>(
    @SerialName("responseCode") val responseCode: String,
    @SerialName("result") val result: T? = null,
    ...
)
```

**DTO 생성 여부 판단:**

| 응답 형태 | Service 반환 타입 | DataSource 패턴 | 반환 타입 |
|-----------|------------------|-----------------|-----------|
| 응답 없음 / 성공여부만 중요 | `BaseResponse<Unit>` 불가 → `Response<Unit>` 사용 | `isSuccessful` 체크 | `Result<Unit>` |
| 단일 primitive (`Boolean` 등) | `BaseResponse<Boolean>` | `isSuccessful` + `body()?.result` 추출 | `Result<Boolean>` |
| 복잡한 객체 (여러 필드) | `BaseResponse<Response<Name>Dto>` | `.parse()` | `Result<DomainModel>` |

> **중요:** `result` 필드가 primitive인 경우 제네릭에 `Unit`을 넣으면 안 된다. kotlinx.serialization은 `Unit`을 `{}` (빈 객체)로 역직렬화하는데, 서버가 `true` 같은 primitive를 보내면 `JsonDecodingException`이 발생한다. 실제 서버 응답 타입(`Boolean` 등)을 그대로 제네릭에 사용하고, DataSource에서 `body()?.result`로 값을 추출해 `Result<Boolean>`을 반환한다.

---

## Step 0: 스펙 파악

API 입력에서 다음을 추출한다:
- HTTP Method (GET / POST / PUT / DELETE / PATCH)
- URL path 전체 (예: `/api/locations/is-service-region`)
- Query Parameters 목록 (이름, 타입, 필수 여부)
- Request Body 필드 목록 (있을 때만)
- Response 필드 목록 (이름, 타입, 복잡도)

**네이밍 규칙** (URL 마지막 segment 기준):
- `is-service-region` → PascalCase: `IsServiceRegion`, camelCase: `isServiceRegion`, CONST: `IS_SERVICE_REGION`

**대상 파일 판단** (URL prefix 기준):
- `/api/locations/...` → `LocationsService`, `LocationsRemoteDataSource`, `LocationsRepository`, `LocationsRepositoryImpl`
- `/api/users/...` → `AuthService` 등 기존 파일 확인 후 결정

## Step 1: 기존 파일 파악

대상 Service, DataSource, Repository, RepositoryImpl 파일을 읽어 기존 패턴을 확인한다.

## Step 2: Constraints.kt — 경로 상수 추가

`ApiConstraints` 오브젝트에 상수 추가. 이미 존재하면 재사용.

```kotlin
const val IS_SERVICE_REGION = "is-service-region"
```

## Step 3: Response DTO 생성 (복잡한 객체일 때만)

응답이 여러 필드를 가진 객체일 때만 `response/<domain>/Response<Name>Dto.kt` 생성.

```kotlin
@Keep
@Serializable
data class Response<Name>Dto(
    @SerialName("fieldName")
    val fieldName: FieldType
)
```

단순 primitive / 성공여부만 중요한 경우 → DTO 생성 안 함.

## Step 4: Request DTO 생성 (POST/PUT 일 때만)

```kotlin
@Keep
@Serializable
data class Request<Name>Dto(
    @SerialName("fieldName")
    val fieldName: FieldType
)
```

## Step 5: Service 인터페이스 — 엔드포인트 추가

**Unit 패턴** (성공여부만 중요하거나 단순 primitive 응답):
```kotlin
@GET("$API/$DOMAIN/$ENDPOINT_CONST")
suspend fun get<Name>(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double
): BaseResponse<Unit>
```

**DTO 패턴** (복잡한 객체 응답):
```kotlin
@GET("$API/$DOMAIN/$ENDPOINT_CONST")
suspend fun get<Name>(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double
): BaseResponse<Response<Name>Dto>
```

**POST + Body**:
```kotlin
@POST("$API/$DOMAIN/$ENDPOINT_CONST")
suspend fun post<Name>(
    @Body request: Request<Name>Dto
): Response<Unit>
```

## Step 6: RemoteDataSource — 메서드 추가

**Boolean 패턴** — `isSuccessful` + `body()?.result` 추출:
```kotlin
suspend fun get<Name>(lat: Double, lon: Double): Result<Boolean> {
    val response = <service>.get<Name>(lat = lat, lon = lon)
    return if (response.isSuccessful) {
        Result.success(response.body()?.result ?: false)
    } else {
        Result.failure(Exception("Failed"))
    }
}
```

**DTO 패턴** — `.parse()` 사용:
```kotlin
suspend fun get<Name>(lat: Double, lon: Double): Result<Response<Name>Dto> =
    <service>.get<Name>(lat = lat, lon = lon).parse()
```

## Step 7: Repository 인터페이스 — 메서드 선언

```kotlin
// Unit 패턴
suspend fun get<Name>(lat: Double, lon: Double): Result<Unit>

// 도메인 모델 패턴
suspend fun get<Name>(lat: Double, lon: Double): Result<DomainModel>
```

## Step 8: RepositoryImpl — 메서드 구현

**Unit 패턴** — 직접 위임:
```kotlin
override suspend fun get<Name>(lat: Double, lon: Double): Result<Unit> =
    <dataSource>.get<Name>(lat = lat, lon = lon)
```

**도메인 모델 패턴** — `.mapCatching` + `toDomain()`:
```kotlin
override suspend fun get<Name>(lat: Double, lon: Double): Result<DomainModel> =
    <dataSource>.get<Name>(lat = lat, lon = lon)
        .mapCatching { it.toDomain() }
```

복잡한 도메인 모델이 필요한 경우 `data/mapper/todomain/`에 `toDomain()` 확장함수 추가.

## Step 9: UseCase 생성

`GetAddressFromCoordinatesUseCase` 패턴을 따라 생성.

```kotlin
@Singleton
class Get<Name>UseCase @Inject constructor(
    private val <domain>Repository: <Domain>Repository
) : NetworkRequestUseCase<Get<Name>UseCase.Params, Unit>() {

    data class Params(val lat: Double, val lon: Double)

    suspend operator fun invoke(lat: Double, lon: Double): Result<Unit> =
        invoke(Params(lat, lon))

    override suspend fun apiCall(params: Params): Result<Unit> =
        <domain>Repository.get<Name>(lat = params.lat, lon = params.lon)

    override fun apiExceptionMapper(errorCode: String): ErrorControlFailureException {
        return when (errorCode) {
            TOK_001 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            TOK_002 -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED)
            USR_002 -> ErrorControlFailureException.NavigateAndShowToastException(
                toastMessage = API_ERROR_LOGIN_TOKEN_EXPIRED,
                route = Route.Login
            )
            INTERNAL_SERVER_ERROR -> ErrorControlFailureException.ShowToastException(toastMessage = API_ERROR_NETWORK_FAILURE)
            else -> ErrorControlFailureException.ShowToastException(API_ERROR_NETWORK_FAILURE)
        }
    }
}
```

필수 imports:
```kotlin
import com.depromeet.team6.domain.Auth.TOK_001
import com.depromeet.team6.domain.Auth.TOK_002
import com.depromeet.team6.domain.Auth.USR_002
import com.depromeet.team6.domain.Network.INTERNAL_SERVER_ERROR
import com.depromeet.team6.domain.ToastMessage.API_ERROR_LOGIN_TOKEN_EXPIRED
import com.depromeet.team6.domain.ToastMessage.API_ERROR_NETWORK_FAILURE
import com.depromeet.team6.domain.usecase.base.NetworkRequestUseCase
import com.depromeet.team6.presentation.model.exception.ErrorControlFailureException
import com.depromeet.team6.presentation.model.route.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
```

## 완료 보고

구현 완료 후 아래 표로 요약한다:

| 레이어 | 파일 | 변경 내용 |
|--------|------|-----------|
| Constraints | `Constraints.kt` | 상수 추가 |
| Response DTO | `Response<Name>Dto.kt` | 신규 생성 (복잡한 객체일 때만) |
| Service | `<X>Service.kt` | 엔드포인트 추가 |
| DataSource | `<X>RemoteDataSource.kt` | 메서드 추가 |
| Repository Interface | `<X>Repository.kt` | 선언 추가 |
| Repository Impl | `<X>RepositoryImpl.kt` | 구현 추가 |
| UseCase | `Get<Name>UseCase.kt` | 신규 생성 |

package com.depromeet.team6.domain.usecase

// class GetCurrentLatLngUseCase @Inject constructor(
//    private val repository : LocationsRepository
// ) {
//    suspend operator fun invoke() : LatLng {
//        val currentLatLng = repository.getCurrentLatLng()
//        val latitude = currentLatLng.latitude
//        val longitude = currentLatLng.longitude
//        // 위도 또는 경도가 비정상일 경우 기본 위치로 설정
//        if (latitude <= 0 || longitude <= 0) {
//            Timber.e("위도, 경도가 0보다 작음")
//            return (LatLng(DEFAULT_LAT, DEFAULT_LNG))
//        } else {
//            return currentLatLng
//        }
//    }
// }

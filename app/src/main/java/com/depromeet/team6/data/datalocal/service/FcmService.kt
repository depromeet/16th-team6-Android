package com.depromeet.team6.data.datalocal.service

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FcmService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e(TAG, "성공적으로 토큰을 저장함 $token")

        // TODO : 서버에 토큰 저장 API 연동
    }
}
package com.depromeet.team6

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import androidx.appcompat.app.AppCompatDelegate
import com.depromeet.team6.BuildConfig.KAKAO_NATIVE_APP_KEY
import com.depromeet.team6.data.datalocal.manager.LockServiceManager
import com.depromeet.team6.presentation.util.amplitude.AmplitudeUtils.initAmplitude
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@HiltAndroidApp
class Team6App : Application() {

    @Inject
    lateinit var lockServiceManager: LockServiceManager

    override fun onCreate() {
        super.onCreate()
        setDarkMode()
        setKakao()
        setTimber()
        initAmplitude(applicationContext)
        lockServiceManager.scheduleLocationCheck()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setKakao() {
        getKeyHash()
        KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY)
    }

    private fun getKeyHash() {
        Timber.d("getKeyHash key hash: ${Utility.getKeyHash(this)}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in packageInfo.signingInfo?.apkContentsSigners!!) {
                try {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Timber.d("getKeyHash key hash: ${Base64.encodeToString(md.digest(), Base64.NO_WRAP)}")
                } catch (e: NoSuchAlgorithmException) {
                    Timber.w("getKeyHash Unable to get MessageDigest. signature=$signature", e)
                }
            }
        }
    }

    private fun setTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

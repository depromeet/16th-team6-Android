package com.depromeet.team6.data.datalocal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.depromeet.team6.domain.usecase.GetTaxiCostUseCase
import com.depromeet.team6.presentation.ui.lock.LockScreenNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LockReceiver : BroadcastReceiver() {
    private lateinit var navigator: LockScreenNavigator
    private lateinit var taxiCostUseCase: GetTaxiCostUseCase

    fun initialize(navigator: LockScreenNavigator, taxiCostUseCase: GetTaxiCostUseCase) {
        this.navigator = navigator
        this.taxiCostUseCase = taxiCostUseCase
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                if (::navigator.isInitialized) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val taxiCost = taxiCostUseCase.getLastSavedTaxiCost()
                            navigator.navigateToLockScreen(context, taxiCost)
                        } catch (e:Exception) {
                            navigator.navigateToLockScreen(context, 0)
                        }
                    }
                }
            }
        }
    }
}

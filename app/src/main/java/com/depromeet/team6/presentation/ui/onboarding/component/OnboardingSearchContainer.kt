package com.depromeet.team6.presentation.ui.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingSearchContainer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        OnboardingSearchTextField()
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)) {
            OnboardingSelectLocationButton() { }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun OnboardingSearchContainerPreview() {
    OnboardingSearchContainer()
}

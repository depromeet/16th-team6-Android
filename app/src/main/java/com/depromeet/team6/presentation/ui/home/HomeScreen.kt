package com.depromeet.team6.presentation.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose

@Composable
fun HomeScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(modifier = modifier.padding(padding).fillMaxSize()) {
        TMapViewCompose() // Replace with your actual API key
    }
}

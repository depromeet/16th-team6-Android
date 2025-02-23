package com.depromeet.team6.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.presentation.ui.login.LoginRoute
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.Team6Theme.colors
import com.depromeet.team6.ui.theme.Team6Theme.typography
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Team6Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginRoute(
                        modifier = Modifier.padding(innerPadding),
                        navigateToOnboarding = {},
                        navigateToHome = {}
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "슬랙 알림 진짜 안가용가리?! ㅠㅠ",
            modifier = modifier
        )
        Text(
            text = "슬랙 알림 이제 와용가리 ~ + 컬러, 폰트 적용 테스트",
            modifier = modifier,
            color = colors.greyLink,
            style = typography.bodySemiBold12
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Team6Theme {
        Greeting("Android")
    }
}

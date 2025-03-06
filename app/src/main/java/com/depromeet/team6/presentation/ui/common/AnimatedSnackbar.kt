package com.depromeet.team6.presentation.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.defaultTeam6Colors

// TODO : 나중에 다시 브랜치 파서 완성
@Composable
fun AnimatedSnackbar(
    showSnackbar: Boolean,
    hostState: SnackbarHostState,
    onDismiss: () -> Unit
) {
    SnackbarHost(
        hostState = hostState
    ) { data ->
        AnimatedVisibility(
            visible = showSnackbar,
            enter = slideInVertically(
                initialOffsetY = { -it }, // 위에서 아래로 등장
                animationSpec = tween(300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it }, // 아래에서 위로 사라짐
                animationSpec = tween(300)
            )
        ) {
            Snackbar(
                snackbarData = data,
                containerColor = defaultTeam6Colors.black,
                contentColor = defaultTeam6Colors.white,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnackbarTouchExample() {
//    var showSnackbar by remember { mutableStateOf(false) }
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectTapGestures {
//                    // 화면을 터치하면 Snackbar를 토글
//                    showSnackbar = !showSnackbar
//                    if (showSnackbar) {
//                        coroutineScope.launch {
//                            // Snackbar 표시 (코루틴 내에서 호출)
//                            snackbarHostState.showSnackbar("This is a snackbar!")
//                        }
//                    }
//                }
//            }
//    ) {
//        // AnimatedSnackbar 표시
//        AnimatedSnackbar(
//            showSnackbar = showSnackbar,
//            onDismiss = { showSnackbar = false }
//        )
//    }
}

package com.depromeet.team6.presentation.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.depromeet.team6.R

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .paint(
                painter = BitmapPainter(ImageBitmap.imageResource(R.drawable.img_login_background)),
                contentScale = ContentScale.Crop
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_login_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}

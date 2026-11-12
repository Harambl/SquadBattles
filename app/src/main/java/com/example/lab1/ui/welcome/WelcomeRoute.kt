package com.example.lab1.ui.welcome

import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(
    onStartClick: () -> Unit
) {
    WelcomeScreen(
        onStartClick = onStartClick
    )
}
package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.lab1.ui.battle.BattleScreen
import com.example.lab1.ui.battle.BattleViewModel
import com.example.lab1.ui.preparation.PreparationScreen
import com.example.lab1.ui.preparation.PreparationViewModel
import com.example.lab1.ui.welcome.WelcomeScreen

sealed class Screen {
    object Welcome : Screen()
    object Preparation : Screen()
    object Battle : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }


    val preparationViewModel = remember { PreparationViewModel() }
    var battleViewModel by remember { mutableStateOf<BattleViewModel?>(null) }

    when (currentScreen) {
        is Screen.Welcome -> {
            WelcomeScreen(
                onStartClick = { currentScreen = Screen.Preparation }
            )
        }
        is Screen.Preparation -> {
            PreparationScreen(
                viewModel = preparationViewModel,
                onStartBattle = { playerTeam, enemyTeam ->
                    battleViewModel = BattleViewModel(playerTeam, enemyTeam)
                    currentScreen = Screen.Battle
                }
            )
        }
        is Screen.Battle -> {
            battleViewModel?.let { vm ->
                BattleScreen(
                    viewModel = vm,
                    onBattleEnd = {
                        battleViewModel = null
                        currentScreen = Screen.Preparation
                    }
                )
            }
        }
    }
}
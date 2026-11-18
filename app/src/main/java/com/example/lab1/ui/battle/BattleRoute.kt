package com.example.lab1.ui.battle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BattleRoute(
    playerTeam: List<Pair<String, String>>,
    enemyTeam: List<Pair<String, String>>,
    onBattleEnd: () -> Unit
) {
    val viewModel: BattleViewModel = viewModel()

    LaunchedEffect(playerTeam, enemyTeam) {
        if (viewModel.state.team1.isEmpty()) {
            viewModel.startBattle(playerTeam, enemyTeam)
        }
    }

    BattleScreen(
        state = viewModel.state,
        onAttackClick = { viewModel.attack() },
        onDefendClick = { viewModel.defend() },
        onSpecClick = { viewModel.spec() },
        onSwitchHeroClick = { viewModel.switchHero() },
        onRestartClick = {
            viewModel.restart()
            onBattleEnd()
        }
    )
}
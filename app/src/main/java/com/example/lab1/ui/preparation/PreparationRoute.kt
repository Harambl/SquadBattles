package com.example.lab1.ui.preparation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab1.ui.states.PreparationUiState

@Composable
fun PreparationRoute(
    viewModel: PreparationViewModel = viewModel(),
    onStartBattle: (List<Pair<String, String>>, List<Pair<String, String>>) -> Unit
) {
    PreparationScreen(
        state = PreparationUiState(
            playerSlots = viewModel.playerSlots,
            presetTeams = viewModel.presetTeams,
            selectedEnemyIndex = viewModel.selectedEnemyIndex,
            canStart = viewModel.canStartBattle()
        ),
        onTypeChange = { index, type -> viewModel.updateSlotType(index, type) },
        onNameChange = { index, name -> viewModel.updateSlotName(index, name) },
        onRandomizeName = { index -> viewModel.randomizeSlotName(index) },
        onSelectEnemy = { index -> viewModel.selectPresetTeam(index) },
        onStartBattle = {
            onStartBattle(viewModel.getFinalPlayerTeam(), viewModel.enemyTeam)
        }
    )
}


package com.example.lab1.ui.preparation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab1.ui.states.PreparationUiState

@Composable
fun PreparationRoute(
    viewModel: PreparationViewModel = viewModel(),
    onStartBattle: (List<Pair<String, String>>, List<Pair<String, String>>) -> Unit
) {

    val uiState = PreparationUiState(
        playerSlots = viewModel.playerSlots,
        currentMode = viewModel.currentMode,
        challengesState = viewModel.challengesState,
        presetTeams = viewModel.presetTeams,
        selectedEnemyIndex = viewModel.selectedEnemyIndex,
        selectedChallengeId = viewModel.selectedChallengeId,
        canStartBattle = viewModel.canStartBattle()
    )

    PreparationScreen(
        state = uiState,
        onSlotTypeChange = { index, type -> viewModel.updateSlotType(index, type) },
        onSlotNameChange = { index, name -> viewModel.updateSlotName(index, name) },
        onSlotNameRandomize = { index -> viewModel.randomizeSlotName(index) },
        onModeChange = { mode -> viewModel.switchMode(mode) },
        onPresetSelect = { index -> viewModel.selectPresetTeam(index) },
        onChallengeSelect = { challenge -> viewModel.selectChallenge(challenge) },
        onNetworkRetry = { viewModel.retryLoad() },
        onStartBattle = {
            onStartBattle(viewModel.getFinalPlayerTeam(), viewModel.enemyTeam)
        }
    )
}


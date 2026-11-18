package com.example.lab1.ui.states

import com.example.lab1.network.Challenge
import com.example.lab1.network.NetworkResult
import com.example.lab1.ui.preparation.BattleMode

data class PreparationUiState(
    val playerSlots: List<HeroSlot>,
    val currentMode: BattleMode,
    val challengesState: NetworkResult<List<Challenge>>,
    val presetTeams: List<TeamConfig>,
    val selectedEnemyIndex: Int,
    val selectedChallengeId: String?,
    val canStartBattle: Boolean
)

data class HeroSlot(val type: String, val name: String)

data class TeamConfig(val name: String, val members: List<Pair<String, String>>)
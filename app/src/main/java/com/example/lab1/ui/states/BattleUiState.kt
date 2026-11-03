package com.example.lab1.ui.states

data class BattleUiState(
    val team1: List<HeroUiState> = emptyList(),
    val team2: List<HeroUiState> = emptyList(),
    val currentTeam1Index: Int = 0,
    val currentTeam2Index: Int = 0,
    val isTeam1Turn: Boolean = true,
    val winnerMessage: String? = null,
    val battleLog: String = ""
)
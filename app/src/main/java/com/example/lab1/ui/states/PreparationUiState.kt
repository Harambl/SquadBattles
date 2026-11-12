package com.example.lab1.ui.states

data class PreparationUiState(
    val playerSlots: List<HeroSlot>,
    val presetTeams: List<TeamConfig>,
    val selectedEnemyIndex: Int,
    val canStart: Boolean
)

data class HeroSlot(val type: String, val name: String)

data class TeamConfig(val name: String, val members: List<Pair<String, String>>)
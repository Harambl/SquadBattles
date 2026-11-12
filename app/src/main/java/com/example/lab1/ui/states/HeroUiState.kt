package com.example.lab1.ui.states

data class HeroUiState(
    val name: String,
    val maxHealth: Int,
    val health: Int,
    val isDefended: Boolean,
    val isInvulnerable: Boolean,
    val turnsRemaining: Int,
    val specUses: Int,
    val heroType: String
) {
    val isAlive: Boolean get() = health > 0
}
package com.example.lab1.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TeamMember(val type: String, val name: String)

@Serializable
data object WelcomeKey : NavKey

@Serializable
data object PreparationKey : NavKey

@Serializable
data class BattleKey(
    val playerTeam: List<TeamMember>,
    val enemyTeam: List<TeamMember>
) : NavKey
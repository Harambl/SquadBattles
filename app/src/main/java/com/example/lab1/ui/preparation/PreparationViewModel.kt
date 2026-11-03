package com.example.lab1.ui.preparation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lab1.utils.NameGenerator

data class TeamConfig(
    val name: String,
    val members: List<Pair<String, String>>
)

class PreparationViewModel : ViewModel() {

    var playerTeam: List<Pair<String, String>> by mutableStateOf(emptyList())
        private set

    var enemyTeam: List<Pair<String, String>> by mutableStateOf(emptyList())
        private set

    var selectedEnemyIndex: Int by mutableIntStateOf(-1)
        private set

    val presetTeams: List<TeamConfig> = listOf(
        TeamConfig("Сбалансированный", NameGenerator.generateTeam(3)),
        TeamConfig("Лучники", listOf(
            "Archer" to NameGenerator.generateArcherName(),
            "Archer" to NameGenerator.generateArcherName(),
            "Archer" to NameGenerator.generateArcherName()
        )),
        TeamConfig("Воины", listOf(
            "Warrior" to NameGenerator.generateWarriorName(),
            "Warrior" to NameGenerator.generateWarriorName(),
            "Warrior" to NameGenerator.generateWarriorName()
        )),
        TeamConfig("Маги", listOf(
            "Mage" to NameGenerator.generateMageName(),
            "Mage" to NameGenerator.generateMageName(),
            "Mage" to NameGenerator.generateMageName()
        ))
    )

    init {
        generateRandomTeam()
    }

    fun generateRandomTeam() {
        playerTeam = NameGenerator.generateTeam(3)
    }

    fun selectPresetTeam(index: Int) {
        if (index in presetTeams.indices) {
            selectedEnemyIndex = index
            enemyTeam = presetTeams[index].members
        }
    }

    fun canStartBattle(): Boolean =
        playerTeam.isNotEmpty() && enemyTeam.isNotEmpty() && selectedEnemyIndex != -1
}
package com.example.lab1.ui.preparation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lab1.utils.NameGenerator
import com.example.lab1.ui.states.HeroSlot
import com.example.lab1.ui.states.TeamConfig

class PreparationViewModel : ViewModel() {

    var playerSlots by mutableStateOf(
        listOf(
            HeroSlot("Archer", NameGenerator.generateArcherName()),
            HeroSlot("Warrior", NameGenerator.generateWarriorName()),
            HeroSlot("Mage", NameGenerator.generateMageName())
        )
    )
        private set

    var enemyTeam by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    var selectedEnemyIndex by mutableStateOf(-1)
        private set

    val presetTeams = listOf(
        TeamConfig("Сбалансированный", NameGenerator.generateTeam(3)),
        TeamConfig("Отряд Лучников", List(3) { "Archer" to NameGenerator.generateArcherName() }),
        TeamConfig("Отряд Воинов", List(3) { "Warrior" to NameGenerator.generateWarriorName() }),
        TeamConfig("Отряд Магов", List(3) { "Mage" to NameGenerator.generateMageName() })
    )

    fun updateSlotType(index: Int, newType: String) {
        val newName = when (newType) {
            "Archer" -> NameGenerator.generateArcherName()
            "Warrior" -> NameGenerator.generateWarriorName()
            "Mage" -> NameGenerator.generateMageName()
            else -> "Неизвестно"
        }
        playerSlots = playerSlots.toMutableList().apply {
            this[index] = HeroSlot(newType, newName)
        }
    }

    fun updateSlotName(index: Int, newName: String) {
        playerSlots = playerSlots.toMutableList().apply {
            this[index] = this[index].copy(name = newName)
        }
    }

    fun randomizeSlotName(index: Int) {
        val currentType = playerSlots[index].type
        val newName = when (currentType) {
            "Archer" -> NameGenerator.generateArcherName()
            "Warrior" -> NameGenerator.generateWarriorName()
            "Mage" -> NameGenerator.generateMageName()
            else -> "Неизвестно"
        }
        updateSlotName(index, newName)
    }

    fun selectPresetTeam(index: Int) {
        if (index in presetTeams.indices) {
            selectedEnemyIndex = index
            enemyTeam = presetTeams[index].members
        }
    }

    fun canStartBattle(): Boolean = selectedEnemyIndex != -1

    fun getFinalPlayerTeam(): List<Pair<String, String>> {
        return playerSlots.map { slot ->
            val finalName = if (slot.name.isBlank()) {
                when (slot.type) {
                    "Archer" -> NameGenerator.generateArcherName()
                    "Warrior" -> NameGenerator.generateWarriorName()
                    "Mage" -> NameGenerator.generateMageName()
                    else -> "Безымянный"
                }
            } else {
                slot.name
            }
            slot.type to finalName
        }
    }
}
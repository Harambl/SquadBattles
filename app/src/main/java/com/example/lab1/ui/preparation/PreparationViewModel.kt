package com.example.lab1.ui.preparation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1.network.Challenge
import com.example.lab1.network.ChallengesRepository
import com.example.lab1.network.NetworkResult
import com.example.lab1.utils.NameGenerator
import com.example.lab1.ui.states.HeroSlot
import com.example.lab1.ui.states.TeamConfig
import kotlinx.coroutines.launch

enum class BattleMode {
    LOCAL,
    NETWORK
}
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

    var selectedChallengeId by mutableStateOf<String?>(null)
        private set

    var currentMode by mutableStateOf(BattleMode.LOCAL)
        private set

    var challengesState by mutableStateOf<NetworkResult<List<Challenge>>>(NetworkResult.Idle)
        private set

    private var cachedChallenges: List<Challenge>? = null
    val presetTeams = listOf(
        TeamConfig("Случайный", NameGenerator.generateTeam(3)),
        TeamConfig("Отряд Лучников", List(3) { "Archer" to NameGenerator.generateArcherName() }),
        TeamConfig("Отряд Воинов", List(3) { "Warrior" to NameGenerator.generateWarriorName() }),
        TeamConfig("Отряд Магов", List(3) { "Mage" to NameGenerator.generateMageName() })
    )

    private val repository = ChallengesRepository()


    init {
        loadChallenges()
    }

    fun switchMode(mode: BattleMode) {
        currentMode = mode
        if (mode == BattleMode.NETWORK && cachedChallenges == null) {
            loadChallenges()
        }
    }

    fun loadChallenges() {
        challengesState = NetworkResult.Loading
        viewModelScope.launch {
            val result = repository.fetchChallengesWithRetry()
            challengesState = result
            if (result is NetworkResult.Success) {
                cachedChallenges = result.data
            }
        }
    }

    fun retryLoad() {
        loadChallenges()
    }

    fun selectChallenge(challenge: Challenge) {
        selectedChallengeId = challenge.id
        val mappedTeam = challenge.members.map { member ->
            val finalName = if (challenge.type == "boss" && !member.name.isNullOrBlank()) {
                member.name
            } else {
                when (member.heroClass) {
                    "Archer" -> NameGenerator.generateArcherName()
                    "Warrior" -> NameGenerator.generateWarriorName()
                    "Mage" -> NameGenerator.generateMageName()
                    else -> "Неизвестно"
                }
            }
            member.heroClass to finalName
        }

        enemyTeam = mappedTeam
        selectedEnemyIndex = -1
    }

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
            selectedChallengeId = null
            enemyTeam = presetTeams[index].members
        }
    }

    fun canStartBattle(): Boolean = selectedEnemyIndex != -1 || selectedChallengeId != null

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
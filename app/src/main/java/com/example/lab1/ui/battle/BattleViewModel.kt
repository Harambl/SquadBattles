package com.example.lab1.ui.battle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lab1.domain.Archer
import com.example.lab1.domain.Mage
import com.example.lab1.domain.Person
import com.example.lab1.domain.Warrior
import com.example.lab1.ui.states.HeroUiState
import com.example.lab1.ui.states.BattleUiState

class BattleViewModel(
    private val team1Config: List<Pair<String, String>>,
    private val team2Config: List<Pair<String, String>>
) : ViewModel() {

    private var team1: List<Person> = emptyList()
    private var team2: List<Person> = emptyList()

    var state: BattleUiState by mutableStateOf(BattleUiState())
        private set

    init {
        initializeTeams()
    }

    private fun initializeTeams() {
        team1 = team1Config.map { createPerson(it.first, it.second) }
        team2 = team2Config.map { createPerson(it.first, it.second) }

        state = BattleUiState(
            team1 = team1.map { it.toUiState() },
            team2 = team2.map { it.toUiState() }
        )
    }

    private fun createPerson(type: String, name: String): Person {
        return when (type) {
            "Archer" -> Archer(name)
            "Warrior" -> Warrior(name)
            "Mage" -> Mage(name)
            else -> Archer(name)
        }
    }

    private fun Person.toUiState(): HeroUiState {
        return HeroUiState(
            name = name,
            maxHealth = maxHealth,
            health = health,
            isDefended = isDefended,
            isInvulnerable = isInvulnerable,
            turnsRemaining = turnsRemaining,
            specUses = when (this) {
                is Archer -> countSpec
                is Warrior -> countSpec
                is Mage -> countSpec
                else -> 0
            },
            heroType = getHeroType()
        )
    }

    private fun updateUiState() {
        state = state.copy(
            team1 = team1.map { it.toUiState() },
            team2 = team2.map { it.toUiState() }
        )
    }

    private fun getActiveTeam(): List<Person> = if (state.isTeam1Turn) team1 else team2
    private fun getEnemyTeam(): List<Person> = if (state.isTeam1Turn) team2 else team1

    private fun getActiveIndex(): Int = if (state.isTeam1Turn) state.currentTeam1Index else state.currentTeam2Index
    private fun getEnemyIndex(): Int = if (state.isTeam1Turn) state.currentTeam2Index else state.currentTeam1Index

    private fun findNextAliveIndex(team: List<Person>, startIndex: Int): Int {
        if (team.none { it.isAlive }) return startIndex
        var index = (startIndex + 1) % team.size
        var attempts = 0
        while (!team[index].isAlive && attempts < team.size) {
            index = (index + 1) % team.size
            attempts++
        }
        return index
    }

    private fun resetActiveTeamState() {
        val activeTeam = getActiveTeam()
        activeTeam.forEach {
            it.isDefended = false
        }
    }

    private fun ensureActiveHeroAlive() {
        val isTeam1 = state.isTeam1Turn
        val team = getActiveTeam()
        val currentIndex = getActiveIndex()

        if (team.getOrNull(currentIndex)?.isAlive == false) {
            val newIndex = findNextAliveIndex(team, currentIndex)
            state = if (isTeam1) {
                state.copy(currentTeam1Index = newIndex)
            } else {
                state.copy(currentTeam2Index = newIndex)
            }
        }
    }

    private fun tickTeam(team: List<Person>) {
        team.forEach { it.tick() }
    }

    private fun switchToNextTeam() {
        tickTeam(getActiveTeam())
        state = state.copy(isTeam1Turn = !state.isTeam1Turn)
        resetActiveTeamState()
        ensureActiveHeroAlive()
        updateUiState()
    }

    private fun checkWinCondition(): Boolean {
        val team1Alive = team1.any { it.isAlive }
        val team2Alive = team2.any { it.isAlive }

        return when {
            !team1Alive && !team2Alive -> {
                state = state.copy(winnerMessage = "Ничья! Все пали в бою.")
                true
            }
            !team1Alive -> {
                state = state.copy(winnerMessage = "Команда 2 победила!")
                true
            }
            !team2Alive -> {
                state = state.copy(winnerMessage = "Команда 1 победила!")
                true
            }
            else -> false
        }
    }

    fun attack() {
        if (state.winnerMessage != null) return

        val attacker = getActiveTeam().getOrNull(getActiveIndex()) ?: return
        if (!attacker.isAlive) return

        val enemyTeam = getEnemyTeam()
        val enemyIndex = getEnemyIndex()
        val target = enemyTeam.getOrNull(enemyIndex)

        if (target == null || !target.isAlive) {
            state = state.copy(battleLog = "Противник уже мёртв! Ход переходит дальше.")
            switchToNextTeam()
            return
        }

        attacker.attack(target)
        updateUiState()

        if (!checkWinCondition()) {
            switchToNextTeam()
        } else {
            updateUiState()
        }
    }

    fun defend() {
        if (state.winnerMessage != null) return

        val hero = getActiveTeam().getOrNull(getActiveIndex()) ?: return
        if (!hero.isAlive) return

        hero.defend()
        updateUiState()
        switchToNextTeam()
    }

    fun spec() {
        if (state.winnerMessage != null) return

        val attacker = getActiveTeam().getOrNull(getActiveIndex()) ?: return
        if (!attacker.isAlive) return

        val enemyTeam = getEnemyTeam()

        if (attacker is Mage) {
            attacker.spec(enemyTeam)
            state = state.copy(battleLog = "${attacker.name} использует огненный шар по всем врагам!")
        } else {
            val enemyIndex = getEnemyIndex()
            val target = enemyTeam.getOrNull(enemyIndex)
            if (target != null && target.isAlive) {
                attacker.spec(target)
            } else {
                state = state.copy(battleLog = "Цель недоступна!")
                switchToNextTeam()
                return
            }
        }

        updateUiState()

        if (!checkWinCondition()) {
            switchToNextTeam()
        } else {
            updateUiState()
        }
    }

    fun switchHero() {
        if (state.winnerMessage != null) return

        val isTeam1 = state.isTeam1Turn
        val team = getActiveTeam()
        val currentIndex = getActiveIndex()
        val newIndex = findNextAliveIndex(team, currentIndex)

        state = if (isTeam1) {
            state.copy(currentTeam1Index = newIndex)
        } else {
            state.copy(currentTeam2Index = newIndex)
        }
    }

}
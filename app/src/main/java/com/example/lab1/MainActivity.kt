package com.example.lab1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


abstract class Person(val name: String, val maxHealth: Int) {
    var health: Int = maxHealth
    var isDefended: Boolean = false
    var isInvulnerable: Boolean = false
    val isAlive: Boolean get() = health > 0

    abstract fun attack(target: Person)
    abstract fun special(target: Person)
    abstract fun getHeroType(): String

    fun defend() {
        isDefended = true
        Log.d("Battle", "$name защищается")
    }

    protected fun applyDamage(target: Person, damage: Int) {
        if (target.isInvulnerable) {
            Log.d("Battle", "$name не может атаковать ${target.name} (неуязвим)")
        } else if (target.isDefended) {
            target.health = (target.health - damage / 2).coerceAtLeast(0)
            Log.d("Battle", "$name атакует ${target.name} через блок. Здоровье: ${target.health}")
        } else {
            target.health = (target.health - damage).coerceAtLeast(0)
            Log.d("Battle", "$name атакует ${target.name}. Здоровье: ${target.health}")
        }
    }
}

class Archer(name: String) : Person(name, 70) {
    var countSpecial = 0

    override fun attack(target: Person) = applyDamage(target, 35)

    override fun special(target: Person) {
        if (countSpecial < 2) {
            countSpecial += 1
            if (target.isInvulnerable) {
                Log.d("Battle", "$name не может пронзить ${target.name}")
            } else {
                target.health = (target.health - 20).coerceAtLeast(0)
                Log.d("Battle", "$name пронзает ${target.name}. Здоровье: ${target.health}")
            }
        } else {
            Log.d("Battle", "$name уже использовал спецприем 2 раза!")
        }
    }

    override fun getHeroType() = "Archer"
}

class Warrior(name: String) : Person(name, 110) {
    var countSpecial = 0

    override fun attack(target: Person) = applyDamage(target, 20)

    override fun special(target: Person) {
        if (countSpecial < 2) {
            countSpecial += 1
            isInvulnerable = true
            Log.d("Battle", "$name становится неуязвимым!")
        } else {
            Log.d("Battle", "$name уже использовал спецприем 2 раза!")
        }
    }

    override fun getHeroType() = "Warrior"
}

class Mage(name: String) : Person(name, 80) {
    var countSpecial = 0

    override fun attack(target: Person) = applyDamage(target, 27)

    override fun special(target: Person) {
        if (countSpecial < 2) {
            countSpecial += 1
            if (target.isInvulnerable) {
                Log.d("Battle", "$name не может использовать огненный шар на ${target.name}")
            } else {
                target.health = (target.health - 25).coerceAtLeast(0)
                Log.d("Battle", "$name использует огненный шар на ${target.name}. Здоровье: ${target.health}")
            }
        } else {
            Log.d("Battle", "$name уже использовал спецприем 2 раза!")
        }
    }

    override fun getHeroType() = "Mage"
}


data class HeroUiState(
    val name: String,
    val maxHealth: Int,
    val health: Int,
    val isDefended: Boolean,
    val isInvulnerable: Boolean,
    val specUses: Int,
    val heroType: String
) {
    val isAlive: Boolean get() = health > 0
}

data class BattleUiState(
    val team1: List<HeroUiState>,
    val team2: List<HeroUiState>,
    val currentTeam1Index: Int = 0,
    val currentTeam2Index: Int = 0,
    val isTeam1Turn: Boolean = true,
    val winnerMessage: String? = null
)


class BattleState {
    private var team1: List<Person> = emptyList()
    private var team2: List<Person> = emptyList()

    var state by mutableStateOf(createInitialState())
        private set

    private fun createInitialState(): BattleUiState {
        team1 = listOf(
            Archer("Ви"),
            Warrior("Стен"),
            Mage("Гэндальф")
        )
        team2 = listOf(
            Archer("Леголас"),
            Warrior("Гимли"),
            Mage("Саруман")
        )

        return BattleUiState(
            team1 = team1.map { it.toUiState() },
            team2 = team2.map { it.toUiState() }
        )
    }

    private fun Person.toUiState(): HeroUiState {
        return HeroUiState(
            name = name,
            maxHealth = maxHealth,
            health = health,
            isDefended = isDefended,
            isInvulnerable = isInvulnerable,
            specUses = when (this) {
                is Archer -> countSpecial
                is Warrior -> countSpecial
                is Mage -> countSpecial
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

    private fun getActiveHero(): Person? {
        val team = if (state.isTeam1Turn) team1 else team2
        val index = if (state.isTeam1Turn) state.currentTeam1Index else state.currentTeam2Index
        return team.getOrNull(index)?.takeIf { it.isAlive }
    }

    private fun getEnemyTeam(): List<Person> = if (state.isTeam1Turn) team2 else team1
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

    private fun switchActiveHero() {
        val isTeam1 = state.isTeam1Turn
        val team = if (isTeam1) team1 else team2
        val currentIndex = if (isTeam1) state.currentTeam1Index else state.currentTeam2Index
        val newIndex = findNextAliveIndex(team, currentIndex)

        state = if (isTeam1) {
            state.copy(currentTeam1Index = newIndex)
        } else {
            state.copy(currentTeam2Index = newIndex)
        }
        updateUiState()
    }

    private fun ensureActiveHeroAlive() {
        val isTeam1 = state.isTeam1Turn
        val team = if (isTeam1) team1 else team2
        val currentIndex = if (isTeam1) state.currentTeam1Index else state.currentTeam2Index

        if (team.getOrNull(currentIndex)?.isAlive == false) {
            val newIndex = findNextAliveIndex(team, currentIndex)
            state = if (isTeam1) {
                state.copy(currentTeam1Index = newIndex)
            } else {
                state.copy(currentTeam2Index = newIndex)
            }
            updateUiState()
        }
    }

    private fun checkWinCondition() {
        val team1Alive = team1.any { it.isAlive }
        val team2Alive = team2.any { it.isAlive }

        if (!team1Alive && !team2Alive) {
            state = state.copy(winnerMessage = "Ничья! Все пали в бою.")
        } else if (!team1Alive) {
            state = state.copy(winnerMessage = "Команда 2 победила!")
        } else if (!team2Alive) {
            state = state.copy(winnerMessage = "Команда 1 победила!")
        }
    }

    private fun endTurn() {
        checkWinCondition()
        if (state.winnerMessage != null) return


        state = state.copy(isTeam1Turn = !state.isTeam1Turn)


        val newActiveTeam = if (state.isTeam1Turn) team1 else team2
        newActiveTeam.forEach { it.isDefended = false }
        newActiveTeam.forEach { it.isInvulnerable = false }

        ensureActiveHeroAlive()
        updateUiState()
    }

    fun attack() {
        if (state.winnerMessage != null) return
        val attacker = getActiveHero() ?: return
        val enemyTeam = getEnemyTeam()
        val enemyIndex = getEnemyIndex()
        val target = enemyTeam.getOrNull(enemyIndex)?.takeIf { it.isAlive } ?: return

        attacker.attack(target)
        updateUiState()
        endTurn()
    }

    fun defend() {
        if (state.winnerMessage != null) return
        val hero = getActiveHero() ?: return

        hero.defend()
        updateUiState()
        endTurn()
    }

    fun special() {
        if (state.winnerMessage != null) return
        val attacker = getActiveHero() ?: return
        val enemyTeam = getEnemyTeam()
        val enemyIndex = getEnemyIndex()
        val target = enemyTeam.getOrNull(enemyIndex)?.takeIf { it.isAlive } ?: return

        attacker.special(target)
        updateUiState()
        endTurn()
    }


    fun switchHero() {
        if (state.winnerMessage != null) return
        switchActiveHero()
    }

    fun restart() {
        state = createInitialState()
    }
}

@Composable
fun HeroCard(hero: HeroUiState, isActive: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isActive) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.size(120.dp)
        ) {
            Image(
                painter = when (hero.heroType)
                {"Archer" -> painterResource(R.drawable.archer)
                    "Warrior" -> painterResource(R.drawable.warrior)
                    "Mage" -> painterResource(R.drawable.wizard)
                    else -> painterResource(R.drawable.que)
                },
                contentDescription = hero.name,
                modifier = Modifier.size(240.dp),
                colorFilter = if (hero.isAlive) null
                else ColorFilter.colorMatrix(
                    ColorMatrix().apply
                { setToSaturation(0f) }),
                alpha = if (hero.isAlive) 1f else 0.5f
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = hero.name + if (isActive) " ️ (Ходит)" else "",
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { hero.health.toFloat() / hero.maxHealth },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = hpColor(hero.health, hero.maxHealth)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${hero.health} / ${hero.maxHealth} HP",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = statusText(hero.health, hero.maxHealth, hero.isDefended, hero.isInvulnerable),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BattleScreen(modifier: Modifier = Modifier) {
    val battle = remember { BattleState() }
    val winner = battle.state.winnerMessage

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "ПОЛЕ БОЯ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text("Команда 1", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
            val hero1 = battle.state.team1.getOrNull(battle.state.currentTeam1Index)
            if (hero1 != null) {
                HeroCard(
                    hero = hero1,
                    isActive = battle.state.isTeam1Turn && hero1.isAlive
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text("Команда 2", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
            val hero2 = battle.state.team2.getOrNull(battle.state.currentTeam2Index)
            if (hero2 != null) {
                HeroCard(
                    hero = hero2,
                    isActive = !battle.state.isTeam1Turn && hero2.isAlive
                )
            }
        }

        if (winner == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val activeTeam = if (battle.state.isTeam1Turn) battle.state.team1 else battle.state.team2
                val activeIndex = if (battle.state.isTeam1Turn) battle.state.currentTeam1Index else battle.state.currentTeam2Index
                val currentHero = activeTeam.getOrNull(activeIndex)

                Text(
                    text = "Ходит: ${currentHero?.name ?: "Нет живых"}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Основные действия
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { battle.attack() }, modifier = Modifier.weight(1f)) { Text("Атака") }
                    Button(onClick = { battle.defend() }, modifier = Modifier.weight(1f)) { Text("Защита") }
                    Button(onClick = { battle.special() }, modifier = Modifier.weight(1f)) { Text("Спец.") }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { battle.switchHero() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(" Сменить героя (${activeTeam.count { it.isAlive }} живых)")
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = winner,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedButton(onClick = { battle.restart() }) { Text("Начать бой заново") }
            }
        }
    }
}

fun statusText(hp: Int, maxHp: Int, isDefended: Boolean, isInvulnerable: Boolean): String = when {
    hp <= 0 -> " Пал в бою"
    isInvulnerable -> "✨ Неуязвим"
    isDefended -> "🛡️ Защищается"
    hp < maxHp * 0.3 -> "🩸 Тяжело ранен"
    hp < maxHp -> "⚔️ Сражается"
    else -> "🛡️ Готов к бою"
}

fun hpColor(hp: Int, maxHp: Int): Color = when {
    hp <= 0 -> Color.Gray
    hp < maxHp * 0.3 -> Color(0xFFC62828)
    hp < maxHp * 0.6 -> Color(0xFFF9A825)
    else -> Color(0xFF2E7D32)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BattleScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.mutableIntStateOf
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
    var health by mutableIntStateOf(maxHealth)
    var isDef by mutableStateOf(false)
    var isInvul by mutableStateOf(false)
    val isAlive: Boolean get() = health > 0

    fun defend() {
        isDef = true
        println("$name защищается")
    }
}

class Archer(name: String) : Person(name, 70) {
    val mainAttr: String = "Agility"
    var countSpec by mutableIntStateOf(0)

    fun attack(target: Person) {
        if (target.isInvul) {
            println("$name не может атаковать ${target.name} (неуязвим)")
        } else if (target.isDef) {
            target.health -= 35 / 2
            println("$name атакует ${target.name} через блок. Здоровье цели: ${target.health}")
        } else {
            target.health -= 35
            println("$name атакует ${target.name}. Здоровье цели: ${target.health}")
        }
    }

    fun spec(target: Person) {
        if (countSpec < 2) {
            countSpec += 1
            if (target.isInvul) {
                println("$name не может пронзить ${target.name}")
            } else {
                target.health -= 20
                println("$name пронзает ${target.name}. Здоровье цели: ${target.health}")
            }
        } else {
            println("$name уже использовал спецприем 2 раза!")
        }
    }
}

class Warrior(name: String) : Person(name, 110) {
    val mainAttr: String = "Strength"
    var countSpec by mutableIntStateOf(0)

    fun attack(target: Person) {
        if (target.isInvul) {
            println("$name не может атаковать ${target.name} (неуязвим)")
        } else if (target.isDef) {
            target.health -= 20 / 2
            println("$name атакует ${target.name} через блок. Здоровье цели: ${target.health}")
        } else {
            target.health -= 20
            println("$name атакует ${target.name}. Здоровье цели: ${target.health}")
        }
    }

    fun spec() {
        if (countSpec < 2) {
            countSpec += 1
            isInvul = true
            println("$name становится неуязвимым!")
        } else {
            println("$name уже использовал спецприем 2 раза!")
        }
    }
}


class BattleState {
    val hero1 = Archer("Ви")
    val hero2 = Warrior("Стен")

    var isHero1Turn by mutableStateOf(true)

    val currentHero: Person get() = if (isHero1Turn) hero1 else hero2
    val targetHero: Person get() = if (isHero1Turn) hero2 else hero1

    init {
        startTurn()
    }

    fun startTurn() {
        currentHero.isDef = false
        currentHero.isInvul = false
    }

    fun attack() {
        if (isGameOver()) return
        val current = currentHero
        val target = targetHero
        if (current is Archer) {
            current.attack(target)
        } else if (current is Warrior) {
            current.attack(target)
        }
        endTurn()
    }

    fun defend() {
        if (isGameOver()) return
        currentHero.defend()
        endTurn()
    }

    fun spec() {
        if (isGameOver()) return
        val current = currentHero
        val target = targetHero
        if (current is Archer) {
            current.spec(target)
        } else if (current is Warrior) {
            current.spec()
        }
        endTurn()
    }

    private fun endTurn() {
        if (isGameOver()) return
        isHero1Turn = !isHero1Turn
        startTurn()
    }

    fun isGameOver(): Boolean = !hero1.isAlive || !hero2.isAlive

    fun getWinner(): String = when {
        !hero1.isAlive && !hero2.isAlive -> "Ничья!"
        !hero1.isAlive -> "${hero2.name} победил!"
        !hero2.isAlive -> "${hero1.name} победил!"
        else -> "Бой продолжается"
    }

    fun restart() {
        hero1.health = hero1.maxHealth
        hero1.isDef = false
        hero1.isInvul = false
        hero1.countSpec = 0

        hero2.health = hero2.maxHealth
        hero2.isDef = false
        hero2.isInvul = false
        hero2.countSpec = 0

        isHero1Turn = true
        startTurn()
    }
}


@Composable
fun HeroCard(hero: Person, isActive: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(if (isActive) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.size(120.dp)
        ) {
            Image(
                painter = if (hero is Warrior) painterResource(R.drawable.warrior)
                          else painterResource(R.drawable.archer),
                contentDescription = hero.name,
                modifier = Modifier.size(240.dp),
                colorFilter = if (hero.isAlive) null
                else ColorFilter.colorMatrix(ColorMatrix().apply
                { setToSaturation(0f) }),
                alpha = if (hero.isAlive) 1f else 0.5f
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = hero.name + " Ходит",
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { hero.health.toFloat() / hero.maxHealth },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = hpColor(hero.health, hero.maxHealth)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${hero.health} / ${hero.maxHealth} HP",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = statusText(hero.health, hero.maxHealth, hero.isDef, hero.isInvul),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BattleScreen(modifier: Modifier = Modifier) {
    val battle = remember { BattleState() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "️ АРЕНА ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            HeroCard(hero = battle.hero1, isActive = battle.isHero1Turn)
            HeroCard(hero = battle.hero2, isActive = !battle.isHero1Turn)
        }

        if (!battle.isGameOver()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Действие: ${battle.currentHero.name}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { battle.attack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Атака")
                    }
                    Button(
                        onClick = { battle.defend() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Защита")
                    }
                    Button(
                        onClick = { battle.spec() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Спец. прием")
                    }
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = battle.getWinner(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedButton(onClick = { battle.restart() }) {
                    Text("Начать бой заново")
                }
            }
        }
    }
}


fun statusText(hp: Int, maxHp: Int, isDef: Boolean, isInvul: Boolean): String = when {
    hp <= 0 -> "💀 Пал в бою"
    isInvul -> "✨ Неуязвим"
    isDef -> "🛡️ Защищается (след. атака слабее)"
    hp < maxHp * 0.3 -> " Тяжело ранен"
    hp <= maxHp -> "⚔️ Сражается"
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
                    BattleScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
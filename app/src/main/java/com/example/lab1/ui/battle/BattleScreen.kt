package com.example.lab1.ui.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lab1.ui.states.BattleUiState
import com.example.lab1.ui.HeroCard

@Composable
fun BattleScreen(
    state: BattleUiState,
    onAttackClick: () -> Unit,
    onDefendClick: () -> Unit,
    onSpecClick: () -> Unit,
    onSwitchHeroClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚔️ АРЕНА ⚔️", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                val hero1 = state.team1.getOrNull(state.currentTeam1Index)
                if (hero1 != null) HeroCard(hero = hero1, isActive = state.isTeam1Turn && hero1.isAlive)

                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(visible = state.battleLog.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(text = state.battleLog, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }

                Spacer(Modifier.height(8.dp))

                val hero2 = state.team2.getOrNull(state.currentTeam2Index)
                if (hero2 != null) HeroCard(hero = hero2, isActive = !state.isTeam1Turn && hero2.isAlive)

                if (state.winnerMessage == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val activeTeam = if (state.isTeam1Turn) state.team1 else state.team2
                        val activeIndex = if (state.isTeam1Turn) state.currentTeam1Index else state.currentTeam2Index
                        val currentHero = activeTeam.getOrNull(activeIndex)

                        Text(text = "Ходит: ${currentHero?.name ?: "Нет живых"}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = onAttackClick, modifier = Modifier.weight(1f)) { Text("Атака") }
                            Button(onClick = onDefendClick, modifier = Modifier.weight(1f)) { Text("Защита") }
                            Button(onClick = onSpecClick, modifier = Modifier.weight(1f)) { Text("Спец.") }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = onSwitchHeroClick, modifier = Modifier.fillMaxWidth()) { Text("🔄 Сменить героя") }
                    }
                }
            }

            if (state.winnerMessage != null) {
                Dialog(onDismissRequest = { }, properties = DialogProperties(dismissOnBackPress = false)) {
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(bottom = 16.dp))
                            Text("Бой окончен!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(16.dp))
                            Text(text = state.winnerMessage, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 24.dp))
                            Button(onClick = onRestartClick, modifier = Modifier.fillMaxWidth()) {
                                Text("Новый бой", modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


package com.example.lab1.ui.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lab1.ui.HeroCard


@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onBattleEnd: () -> Unit
) {
    val winner = viewModel.state.winnerMessage

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "️ АРЕНА ⚔️",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                AnimatedVisibility(
                    visible = viewModel.state.battleLog.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = viewModel.state.battleLog,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                val hero1 = viewModel.state.team1.getOrNull(viewModel.state.currentTeam1Index)
                if (hero1 != null) {
                    HeroCard(
                        hero = hero1,
                        isActive = viewModel.state.isTeam1Turn && hero1.isAlive
                    )
                }

                Spacer(Modifier.height(16.dp))

                val hero2 = viewModel.state.team2.getOrNull(viewModel.state.currentTeam2Index)
                if (hero2 != null) {
                    HeroCard(
                        hero = hero2,
                        isActive = !viewModel.state.isTeam1Turn && hero2.isAlive
                    )
                }

                if (winner == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val activeTeam = if (viewModel.state.isTeam1Turn)
                            viewModel.state.team1 else viewModel.state.team2
                        val activeIndex = if (viewModel.state.isTeam1Turn)
                            viewModel.state.currentTeam1Index else viewModel.state.currentTeam2Index
                        val currentHero = activeTeam.getOrNull(activeIndex)

                        Text(
                            text = "Ходит: ${currentHero?.name ?: "Нет живых"}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.attack() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Атака") }

                            Button(
                                onClick = { viewModel.defend() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Защита") }

                            Button(
                                onClick = { viewModel.spec() },
                                modifier = Modifier.weight(1f)
                            ) { Text("Спец.") }
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.switchHero() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔄 Сменить героя")
                        }
                    }
                }
            }

            if (winner != null) {
                BattleResultDialog(
                    winnerMessage = winner,
                    onDismiss = {
                        // При закрытии диалога возвращаемся в меню подготовки
                        onBattleEnd()
                    }
                )
            }

        }
    }
}

@Composable
fun BattleResultDialog(
    winnerMessage: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Бой окончен!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = winnerMessage,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "В меню подготовки",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

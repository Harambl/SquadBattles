package com.example.lab1.ui.preparation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.states.PreparationUiState

@Composable
fun PreparationScreen(
    state: PreparationUiState,
    onTypeChange: (Int, String) -> Unit,
    onNameChange: (Int, String) -> Unit,
    onRandomizeName: (Int) -> Unit,
    onSelectEnemy: (Int) -> Unit,
    onStartBattle: () -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("🛡️ Сбор отряда", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Text("Ваши герои:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            state.playerSlots.forEachIndexed { index, slot ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = slot.type == "Archer",
                                    onClick = { onTypeChange(index, "Archer") },
                                    label = { Text("🏹") }
                                )
                                FilterChip(
                                    selected = slot.type == "Warrior",
                                    onClick = { onTypeChange(index, "Warrior") },
                                    label = { Text("⚔️") }
                                )
                                FilterChip(
                                    selected = slot.type == "Mage",
                                    onClick = { onTypeChange(index, "Mage") },
                                    label = { Text("🔮") }
                                )
                            }

                            IconButton(onClick = { onRandomizeName(index) }) {
                                Text("🎲", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = slot.name,
                            onValueChange = { onNameChange(index, it) },
                            label = { Text("Имя героя (оставь пустым для случайного)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Выберите отряд противника:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                itemsIndexed(state.presetTeams) { index, preset ->
                    val isSelected = index == state.selectedEnemyIndex
                    Card(
                        onClick = { onSelectEnemy(index) },
                        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(preset.name, fontWeight = FontWeight.Bold)
                                if (isSelected) Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            preset.members.forEach { (type, name) ->
                                Text("  ${when(type) { "Archer" -> "🏹"; "Warrior" -> "⚔️"; "Mage" -> "🔮"; else -> "❓" }} $name", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    onStartBattle()
                },
                enabled = state.canStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚔️ В БОЙ!")
            }
        }
    }
}
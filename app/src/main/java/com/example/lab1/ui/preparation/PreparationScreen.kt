package com.example.lab1.ui.preparation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab1.network.Challenge
import com.example.lab1.network.NetworkResult
import com.example.lab1.ui.states.PreparationUiState
import com.example.lab1.ui.states.TeamConfig

@Composable
fun PreparationScreen(
    state: PreparationUiState,
    onSlotTypeChange: (Int, String) -> Unit,
    onSlotNameChange: (Int, String) -> Unit,
    onSlotNameRandomize: (Int) -> Unit,
    onModeChange: (BattleMode) -> Unit,
    onPresetSelect: (Int) -> Unit,
    onChallengeSelect: (Challenge) -> Unit,
    onNetworkRetry: () -> Unit,
    onStartBattle: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "🛡️ Сбор отряда",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            Text(
                "Ваши герои:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))

            state.playerSlots.forEachIndexed { index, slot ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = slot.type == "Archer",
                                    onClick = { onSlotTypeChange(index, "Archer") },
                                    label = { Text("🏹") }
                                )
                                FilterChip(
                                    selected = slot.type == "Warrior",
                                    onClick = { onSlotTypeChange(index, "Warrior") },
                                    label = { Text("⚔️") }
                                )
                                FilterChip(
                                    selected = slot.type == "Mage",
                                    onClick = { onSlotTypeChange(index, "Mage") },
                                    label = { Text("🔮") }
                                )
                            }
                            IconButton(
                                onClick = { onSlotNameRandomize(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("🎲")
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        OutlinedTextField(
                            value = slot.name,
                            onValueChange = { onSlotNameChange(index, it) },
                            label = { Text("Имя героя") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Выберите отряд противника:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.currentMode == BattleMode.LOCAL,
                    onClick = { onModeChange(BattleMode.LOCAL) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("📱 Локальные") }
                SegmentedButton(
                    selected = state.currentMode == BattleMode.NETWORK,
                    onClick = { onModeChange(BattleMode.NETWORK) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("🌐 Сетевые") }
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (state.currentMode) {
                    BattleMode.LOCAL -> {
                        LocalPresetsContent(
                            presetTeams = state.presetTeams,
                            selectedEnemyIndex = state.selectedEnemyIndex,
                            onSelectEnemy = { onPresetSelect(it) }
                        )
                    }
                    BattleMode.NETWORK -> {
                        NetworkChallengesContent(
                            state = state.challengesState,
                            selectedChallengeId = state.selectedChallengeId,
                            onSelectChallenge = { onChallengeSelect(it) },
                            onRetry = { onNetworkRetry() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onStartBattle() },
                enabled = state.canStartBattle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚔️ В БОЙ!")
            }
        }
    }
}

@Composable
private fun LocalPresetsContent(
    presetTeams: List<TeamConfig>,
    selectedEnemyIndex: Int,
    onSelectEnemy: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(presetTeams) { index, preset ->
            val isSelected = index == selectedEnemyIndex
            Card(
                onClick = { onSelectEnemy(index) },
                colors = if (isSelected)
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                else
                    CardDefaults.cardColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            preset.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (isSelected) {
                            Text(
                                "✓",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                    preset.members.forEach { (type, name) ->
                        Text(
                            "  ${when (type) {
                                "Archer" -> "🏹"
                                "Warrior" -> "⚔️"
                                "Mage" -> "🔮"
                                else -> "❓"
                            }} $name",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkChallengesContent(
    state: NetworkResult<List<Challenge>>,
    selectedChallengeId: String?,
    onSelectChallenge: (Challenge) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is NetworkResult.Idle -> {
            Text("Нажмите для загрузки...")
        }
        is NetworkResult.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Загрузка испытаний из сети...", style = MaterialTheme.typography.bodySmall)
            }
        }
        is NetworkResult.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Ошибка: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                if (state.isRetryable) {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Повторить") }
                }
            }
        }
        is NetworkResult.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(state.data) { challenge ->
                    val isSelected = challenge.id == selectedChallengeId
                    Card(
                        onClick = { onSelectChallenge(challenge) },
                        colors = if (isSelected)
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        else
                            CardDefaults.cardColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    challenge.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (challenge.type == "boss") {
                                        Text("👑", style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (isSelected) {
                                        Text(
                                            "✓",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                            }
                            Text(
                                challenge.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            challenge.members.forEach { member ->
                                Text(
                                    "  • ${member.heroClass} ${if (challenge.type == "boss") "(${member.name})" else "(Случайное имя)"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
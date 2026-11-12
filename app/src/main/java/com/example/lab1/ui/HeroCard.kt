package com.example.lab1.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab1.R
import com.example.lab1.ui.states.HeroUiState

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
            modifier = Modifier.size(100.dp)
        ) {
            Image(
                painter = when (hero.heroType) {
                    "Archer" -> painterResource(R.drawable.archer)
                    "Warrior" -> painterResource(R.drawable.warrior)
                    "Mage" -> painterResource(R.drawable.wizard)
                    else -> painterResource(R.drawable.que)
                },
                contentDescription = hero.name,
                modifier = Modifier.fillMaxSize(),
                colorFilter = if (hero.isAlive) null
                else ColorFilter.colorMatrix(
                    ColorMatrix().apply
                    { setToSaturation(0f) }),
                alpha = if (hero.isAlive) 1f else 0.5f
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = hero.name + if (isActive) " ⚔️" else "",
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { hero.health.toFloat() / hero.maxHealth },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = hpColor(hero.health, hero.maxHealth)
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = "${hero.health}/${hero.maxHealth} HP",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = statusText(hero.health, hero.maxHealth, hero.isDefended, hero.isInvulnerable),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun hpColor(hp: Int, maxHp: Int): Color = when {
    hp <= 0 -> Color.Gray
    hp < maxHp * 0.3 -> Color(0xFFC62828)
    hp < maxHp * 0.6 -> Color(0xFFF9A825)
    else -> Color(0xFF2E7D32)
}

fun statusText(hp: Int, maxHp: Int, isDefended: Boolean, isInvulnerable: Boolean): String = when {
    hp <= 0 -> " Пал в бою"
    isInvulnerable -> "✨ Неуязвим"
    isDefended -> "🛡️ Защищается"
    hp < maxHp * 0.3 -> "🩸 Тяжело ранен"
    hp < maxHp -> "⚔️ Сражается"
    else -> "🛡️ Готов к бою"
}
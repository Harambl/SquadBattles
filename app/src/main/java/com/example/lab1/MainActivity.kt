package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.lab1.navigation.BattleKey
import com.example.lab1.navigation.Navigator
import com.example.lab1.navigation.PreparationKey
import com.example.lab1.navigation.TeamMember
import com.example.lab1.navigation.WelcomeKey
import com.example.lab1.navigation.rememberNavigationState
import com.example.lab1.navigation.toEntries
import com.example.lab1.ui.battle.BattleRoute
import com.example.lab1.ui.preparation.PreparationRoute
import com.example.lab1.ui.welcome.WelcomeRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
fun App() {
    val navigationState = rememberNavigationState(
        startRoute = WelcomeKey,
        topLevelRoutes = setOf(WelcomeKey)
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<WelcomeKey> {
            WelcomeRoute(
                onStartClick = { navigator.navigate(PreparationKey) }
            )
        }

        entry<PreparationKey> {
            PreparationRoute(
                onStartBattle = { playerTeam, enemyTeam ->
                    navigator.navigate(
                        BattleKey(
                            playerTeam = playerTeam.map { TeamMember(it.first, it.second) },
                            enemyTeam = enemyTeam.map { TeamMember(it.first, it.second) }
                        )
                    )
                }
            )
        }

        entry<BattleKey> { key ->
            BattleRoute(
                playerTeam = key.playerTeam.map { it.type to it.name },
                enemyTeam = key.enemyTeam.map { it.type to it.name },
                onBattleEnd = { navigator.goBack() }
            )
        }
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
    )
}
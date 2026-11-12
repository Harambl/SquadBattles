package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab1.ui.battle.BattleRoute
import com.example.lab1.ui.preparation.PreparationRoute
import com.example.lab1.ui.welcome.WelcomeRoute

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Preparation : Screen("preparation")
    object Battle : Screen("battle")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var playerTeam by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var enemyTeam by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeRoute(
                onStartClick = { navController.navigate(Screen.Preparation.route) }
            )
        }

        composable(Screen.Preparation.route) {
            PreparationRoute(
                onStartBattle = { pTeam, eTeam ->
                    playerTeam = pTeam
                    enemyTeam = eTeam
                    navController.navigate(Screen.Battle.route)

                }
            )
        }

        composable(Screen.Battle.route) {
            BattleRoute(
                playerTeam = playerTeam,
                enemyTeam = enemyTeam,
                onBattleEnd = {
                    navController.popBackStack()
                }
            )
        }
    }
}
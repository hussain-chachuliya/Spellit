package com.spellit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spellit.presentation.screens.admin.AdminHomeScreen
import com.spellit.presentation.screens.admin.analytics.AdminAnalyticsScreen
import com.spellit.presentation.screens.admin.pin.AdminPinScreen
import com.spellit.presentation.screens.kid.GameScreen
import com.spellit.presentation.screens.kid.HomeScreen
import com.spellit.presentation.screens.kid.modeselect.ModeSelectScreen
import com.spellit.presentation.screens.kid.trophy.TrophyRoomScreen

@Composable
fun SpellItNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onPlay = { player -> navController.navigate(Routes.modeSelect(player)) },
                onTrophy = { player -> navController.navigate(Routes.trophy(player)) },
                onAdmin = { navController.navigate(Routes.ADMIN_PIN) }
            )
        }

        composable(
            route = Routes.MODE_SELECT,
            arguments = Routes.argBuilderPlayerOnly()
        ) { backStackEntry ->
            val player = backStackEntry.arguments?.getString(Routes.playerArg()).orEmpty()
            ModeSelectScreen(
                playerName = player,
                onBack = { navController.popBackStack() },
                onPick = { mode -> navController.navigate(Routes.game(player, mode)) }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = Routes.argBuilder()
        ) { backStackEntry ->
            val player = backStackEntry.arguments?.getString(Routes.playerArg()).orEmpty()
            GameScreen(
                playerName = player,
                onBackToModes = { navController.popBackStack() },
                onHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(
            route = Routes.TROPHY,
            arguments = Routes.argBuilderPlayerOnly()
        ) { backStackEntry ->
            val player = backStackEntry.arguments?.getString(Routes.playerArg()).orEmpty()
            TrophyRoomScreen(
                playerName = player,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_PIN) {
            AdminPinScreen(
                onBack = { navController.popBackStack() },
                onUnlocked = { navController.navigate(Routes.ADMIN_HOME) }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminHomeScreen(
                onBack = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
    }
}
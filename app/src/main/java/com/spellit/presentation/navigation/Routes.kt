package com.spellit.presentation.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.spellit.domain.model.GameMode

object Routes {
    const val HOME = "home"
    const val MODE_SELECT = "modes?player={player}"
    const val GAME = "game?player={player}&mode={mode}"
    const val TROPHY = "trophy?player={player}"

    const val ADMIN_PIN = "admin_pin"
    const val ADMIN_HOME = "admin_home"

    fun modeSelect(playerName: String) =
        "modes?player=${Uri.encode(playerName)}"

    fun game(playerName: String, mode: GameMode) =
        "game?player=${Uri.encode(playerName)}&mode=${mode.name}"

    fun trophy(playerName: String) =
        "trophy?player=${Uri.encode(playerName)}"

    fun playerArg() = "player"
    fun modeArg() = "mode"

    fun argBuilder(): List<NamedNavArgument> = listOf(
        navArgument(playerArg()) { type = NavType.StringType; defaultValue = "" },
        navArgument(modeArg()) { type = NavType.StringType; defaultValue = GameMode.MEDIUM.name }
    )

    fun argBuilderPlayerOnly(): List<NamedNavArgument> = listOf(
        navArgument(playerArg()) { type = NavType.StringType; defaultValue = "" }
    )
}
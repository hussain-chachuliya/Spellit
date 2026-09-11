package com.spellit.presentation.screens.admin.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.domain.model.GameMode
import com.spellit.domain.model.GameSession
import com.spellit.presentation.components.Chip
import com.spellit.presentation.components.KidButton
import com.spellit.presentation.components.StarRow
import com.spellit.presentation.theme.CoralRed
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.PaleBlue
import com.spellit.presentation.theme.PaleGreen
import com.spellit.presentation.theme.PaleYellow
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.util.FormatUtil
import com.spellit.presentation.viewmodel.AdminAnalyticsViewModel
import com.spellit.presentation.viewmodel.SessionSort
import com.spellit.domain.model.ScoreCalculator
import androidx.compose.material3.AlertDialog

@Composable
fun AdminAnalyticsScreen() {
    val vm: AdminAnalyticsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()
    var confirmDeleteAll by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Analytics & History",
            style = MaterialTheme.typography.headlineMedium,
            color = DeepBlue
        )

        SummaryCards(state = state)

        Spacer(Modifier.height(12.dp))

        // Filters and sort row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(text = "All modes", selected = state.modeFilter == null) { vm.setModeFilter(null) }
            GameMode.entries.forEach { mode ->
                Chip(
                    text = mode.displayName,
                    selected = state.modeFilter == mode
                ) { vm.setModeFilter(if (state.modeFilter == mode) null else mode) }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(text = "All players", selected = state.playerFilter == null) { vm.setPlayerFilter(null) }
            state.players.forEach { player ->
                if (player.isNotBlank()) {
                    Chip(
                        text = player,
                        selected = state.playerFilter == player
                    ) { vm.setPlayerFilter(if (state.playerFilter == player) null else player) }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sort:", fontWeight = FontWeight.SemiBold, color = DeepBlue)
            Spacer(Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip("Newest date", selected = state.sort == SessionSort.DATE_NEW) { vm.setSort(SessionSort.DATE_NEW) }
                Chip("Oldest date", selected = state.sort == SessionSort.DATE_OLD) { vm.setSort(SessionSort.DATE_OLD) }
                Chip("By mode", selected = state.sort == SessionSort.MODE) { vm.setSort(SessionSort.MODE) }
                Chip("By player", selected = state.sort == SessionSort.PLAYER) { vm.setSort(SessionSort.PLAYER) }
            }
            if (state.allSessions.isNotEmpty()) {
                IconButton(onClick = { confirmDeleteAll = true }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = "Delete all sessions",
                        tint = CoralRed,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.filteredSessions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No sessions match.",
                    color = DeepBlue,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.filteredSessions, key = { it.id }) { session ->
                    AdminSessionRow(
                        session = session,
                        onClick = { vm.selectSession(session) },
                        onDelete = { vm.deleteOne(session) }
                    )
                }
            }
        }
    }

    state.selectedSession?.let { session ->
        val vmHost = vm
        AdminSessionDetailOverlay(
            session = session,
            onDismiss = { vm.selectSession(null) },
            onDelete = {
                vmHost.deleteOne(session)
            }
        )
    }

    if (confirmDeleteAll) {
        AlertDialog(
            onDismissRequest = { confirmDeleteAll = false },
            title = { Text("Delete all sessions?") },
            text = { Text("This removes ALL session history permanently. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDeleteAll = false
                    vm.deleteAllSessions()
                }) {
                    Text("Delete All", color = CoralRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteAll = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SummaryCards(state: com.spellit.presentation.viewmodel.AnalyticsUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(label = "Sessions", value = "${state.totalSessions}", color = PaleBlue)
        SummaryCard(label = "Words", value = "${state.totalWords}", color = PaleGreen)
        SummaryCard(label = "Correct", value = "${state.totalCorrect}", color = PaleYellow)
        SummaryCard(label = "Score", value = "${state.totalScore}", color = PaleBlue)
    }
}

@Composable
private fun SummaryCard(label: String, value: String, color: Color) {
    Surface(shape = RoundedCornerShape(16.dp), color = color) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = DeepBlue)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = DeepBlue)
        }
    }
}

@Composable
private fun AdminSessionRow(
    session: GameSession,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = session.playerName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = DeepBlue
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = session.gameMode.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepBlue,
                        modifier = Modifier
                            .background(SkyBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${FormatUtil.formatDate(session.timestamp)} at ${FormatUtil.formatTime(session.timestamp)} · Score ${session.score} · ${session.correctCount}/${session.totalWords} correct",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            StarRow(filledStars = ScoreCalculator.starRating(session.correctCount, session.totalWords), starSize = 20)
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete session",
                    tint = CoralRed,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminSessionDetailOverlay(
    session: GameSession,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "${session.playerName} · ${session.gameMode.displayName}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = DeepBlue
                        )
                        Text(
                            text = "${FormatUtil.formatDate(session.timestamp)} at ${FormatUtil.formatTime(session.timestamp)} · Score ${session.score}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    StarRow(
                        filledStars = ScoreCalculator.starRating(session.correctCount, session.totalWords),
                        starSize = 26
                    )
                }

                Spacer(Modifier.height(12.dp))

                session.results.forEachIndexed { index, result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}.", fontWeight = FontWeight.Bold, color = DeepBlue, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = result.spelling,
                            fontWeight = FontWeight.SemiBold,
                            color = if (result.correct) GrassGreen else CoralRed,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (result.correct) "✔" else "✘",
                            color = if (result.correct) GrassGreen else CoralRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KidButton(
                        text = "Delete Session",
                        onClick = onDelete,
                        color = CoralRed,
                        modifier = Modifier.weight(1f)
                    )
                    KidButton(
                        text = "Close",
                        onClick = onDismiss,
                        color = SkyBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
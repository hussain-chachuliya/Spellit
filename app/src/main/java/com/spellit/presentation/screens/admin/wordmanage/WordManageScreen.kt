package com.spellit.presentation.screens.admin.wordmanage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.spellit.presentation.components.KidButton
import com.spellit.presentation.components.RecordingIndicator
import com.spellit.presentation.theme.CoralRed
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.PaleGreen
import com.spellit.presentation.theme.Parchment
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.viewmodel.WordManageViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun WordManageScreen() {
    val vm: WordManageViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Word Manager",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepBlue,
                modifier = Modifier.weight(1f)
            )
            KidButton(
                text = "+ Add Word",
                onClick = { showAddDialog = true },
                color = GrassGreen
            )
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PaleGreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(
                text = "${state.totalCount} words total · ${state.longWordCount} long words (9+ letters) for Easy mode",
                color = DeepBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.words, key = { it.id }) { word ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(word.spelling, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DeepBlue)
                            Text(
                                text = if (word.audioFileName != null) "🔊 audio saved" else "⚠ no audio",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (word.audioFileName != null) GrassGreen else MaterialTheme.colorScheme.error
                            )
                        }
                        if (word.audioFileName != null) {
                            IconButton(onClick = { vm.previewAudio(word) }) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Preview audio", tint = SkyBlue, modifier = Modifier.size(28.dp))
                            }
                        }
                        IconButton(onClick = { vm.deleteWordAndAudio(word) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete word", tint = CoralRed, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWordDialog(
            vm = vm,
            onSave = { spelling, fileName ->
                vm.addNewWord(spelling, fileName)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

private sealed interface RecordingState {
    object Idle : RecordingState
    data class Recording(val path: String) : RecordingState
    data class Recorded(val path: String) : RecordingState
}

@Composable
private fun AddWordDialog(
    vm: WordManageViewModel,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var spelling by remember { mutableStateOf("") }
    var recording by remember { mutableStateOf<RecordingState>(RecordingState.Idle) }
    var audioFileName by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun stopActiveRecording() {
        val current = recording
        if (current is RecordingState.Recording) {
            scope.launch {
                val path = vm.stopRecording()
                if (path != null) {
                    audioFileName = File(path).name
                    recording = RecordingState.Recorded(path)
                } else {
                    recording = RecordingState.Idle
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            stopActiveRecording()
            onDismiss()
        },
        title = { Text("Add a Word", fontWeight = FontWeight.Bold, color = DeepBlue) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = spelling,
                    onValueChange = { spelling = it },
                    label = { Text("Spelling") },
                    placeholder = { Text("e.g. butterfly") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Step 2: Record the pronunciation", fontWeight = FontWeight.SemiBold, color = DeepBlue)

                when (val r = recording) {
                    is RecordingState.Idle -> {
                        KidButton(
                            text = "🎙 Record",
                            onClick = {
                                val path = vm.createRecordingPath()
                                scope.launch {
                                    vm.startRecording(path)
                                    recording = RecordingState.Recording(path)
                                }
                            },
                            color = CoralRed,
                            enabled = spelling.isNotBlank() && audioFileName == null
                        )
                    }
                    is RecordingState.Recording -> {
                        RecordingIndicator()
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            KidButton(
                                text = "⏹ Stop",
                                onClick = { stopActiveRecording() },
                                color = CoralRed
                            )
                        }
                    }
                    is RecordingState.Recorded -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            KidButton(
                                text = "▶ Preview",
                                onClick = { vm.playAbsolute(r.path) },
                                color = SkyBlue
                            )
                            KidButton(
                                text = "🔁 Retake",
                                onClick = {
                                    scope.launch {
                                        vm.cancelRecording()
                                        audioFileName = null
                                        recording = RecordingState.Idle
                                    }
                                },
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    stopActiveRecording()
                    onSave(spelling, audioFileName)
                },
                enabled = spelling.isNotBlank() && audioFileName != null
            ) {
                Text("Save Word", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                stopActiveRecording()
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}
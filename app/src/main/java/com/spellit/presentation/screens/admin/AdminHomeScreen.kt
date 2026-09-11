package com.spellit.presentation.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spellit.presentation.screens.admin.analytics.AdminAnalyticsScreen
import com.spellit.presentation.screens.admin.settings.SettingsScreen
import com.spellit.presentation.screens.admin.wordmanage.WordManageScreen
import com.spellit.presentation.theme.DeepBlue
import com.spellit.presentation.theme.Parchment

@Composable
fun AdminHomeScreen(
    onBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment)
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepBlue, modifier = Modifier.size(28.dp))
            }
            Text(
                text = "Admin Panel",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepBlue,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Home, contentDescription = "Home", tint = DeepBlue, modifier = Modifier.size(26.dp))
            }
        }

        Box(Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> WordManageScreen()
                1 -> AdminAnalyticsScreen()
                2 -> SettingsScreen()
            }
        }

        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Filled.Sort, contentDescription = null) },
                label = { Text("Words") }
            )
            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Filled.Analytics, contentDescription = null) },
                label = { Text("Analytics") }
            )
            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                label = { Text("Settings") }
            )
        }
    }
}
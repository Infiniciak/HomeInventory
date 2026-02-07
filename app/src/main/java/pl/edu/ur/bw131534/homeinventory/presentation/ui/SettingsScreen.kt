package pl.edu.ur.bw131534.homeinventory.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onExportData: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Wygląd", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            ListItem(
                headlineContent = { Text("Tryb ciemny") },
                supportingContent = { Text("Zmienia motyw kolorystyczny aplikacji") },
                trailingContent = {
                    Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Dane", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            ListItem(
                headlineContent = { Text("Eksportuj bazę danych") },
                supportingContent = { Text("Zapisz kopię zapasową przedmiotów (JSON)") },
                trailingContent = {
                    IconButton(onClick = onExportData) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                }
            )
        }
    }
}
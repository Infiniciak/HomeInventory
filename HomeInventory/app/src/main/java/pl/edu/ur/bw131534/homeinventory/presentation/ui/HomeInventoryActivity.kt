package pl.edu.ur.bw131534.homeinventory.presentation.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.DataManagementViewModel
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.InventoryViewModel
import pl.edu.ur.bw131534.homeinventory.ui.theme.HomeInventoryTheme
import pl.edu.ur.bw131534.homeinventory.utilities.LightSensor
import pl.edu.ur.bw131534.homeinventory.worker.InventoryNotification
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeInventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedulePeriodicReview()
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            HomeInventoryTheme {
                MainAppContent()
                AutomaticBrightnessEffect(this@HomeInventoryActivity)
            }
        }
    }

    @Composable
    fun AutomaticBrightnessEffect(activity: Activity) {
        val context = LocalContext.current
        val lightSensor = remember { LightSensor(context) }
        var lastUpdateTime by remember { mutableLongStateOf(0L) }

        LaunchedEffect(Unit) {
            if (lightSensor.hasLightSensor()) {
                lightSensor.getLightIntensity().collect { lux ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdateTime > 500) {
                        lastUpdateTime = currentTime
                        val newBrightness = when {
                            lux < 10 -> 0.1f
                            lux < 100 -> 0.3f
                            lux < 500 -> 0.6f
                            else -> 1.0f
                        }
                        val layoutParams = activity.window.attributes
                        if (layoutParams.screenBrightness != newBrightness) {
                            layoutParams.screenBrightness = newBrightness
                            activity.window.attributes = layoutParams
                            Log.d("Brightness", "ZMIANA: Lux=$lux -> Jasność=$newBrightness")
                        }
                    }
                }
            }
        }
    }

    private fun schedulePeriodicReview() {
        // Zmieniono na 15 minut (minimum systemowe)
        val reviewRequest = PeriodicWorkRequestBuilder<InventoryNotification>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GeneralInventoryReview",
            ExistingPeriodicWorkPolicy.KEEP,
            reviewRequest
        )
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 101)
            }
        }
    }

    @Composable
    fun MainAppContent() {
        val dataViewModel: DataManagementViewModel = hiltViewModel()
        val inventoryViewModel: InventoryViewModel = hiltViewModel()

        var isScanning by remember { mutableStateOf(false) }
        var isShowingStats by remember { mutableStateOf(false) }
        var isShowingSettings by remember { mutableStateOf(false) }
        var isShowingLocations by remember { mutableStateOf(false) }
        var isShowingWarranties by remember { mutableStateOf(false) }
        var isDarkMode by remember { mutableStateOf(false) }

        HomeInventoryTheme(darkTheme = isDarkMode) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                when {
                    isShowingSettings -> SettingsScreen(
                        onNavigateBack = { isShowingSettings = false },
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { isDarkMode = it },
                        onExportData = { dataViewModel.exportDatabaseToJson() }
                    )
                    isShowingStats -> StatsScreen(onNavigateBack = { isShowingStats = false })
                    isScanning -> Box(modifier = Modifier.fillMaxSize()) {
                        ScannerScreen(onScanSuccess = { isScanning = false })
                        IconButton(
                            onClick = { isScanning = false },
                            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                        ) {
                            Icon(Icons.Default.Close, "Zamknij", tint = Color.White)
                        }
                    }
                    isShowingLocations -> LocationScreen(onNavigateBack = { isShowingLocations = false })
                    isShowingWarranties -> WarrantyScreen(
                        onNavigateBack = { isShowingWarranties = false },
                        inventoryViewModel = inventoryViewModel
                    )
                    else -> HomeScreen(
                        onScanClick = { isScanning = true },
                        onStatsClick = { isShowingStats = true },
                        onSettingsClick = { isShowingSettings = true },
                        onLocationsClick = { isShowingLocations = true },
                        onWarrantiesClick = { isShowingWarranties = true }
                    )
                }
            }
        }

        BackHandler(enabled = isScanning || isShowingStats || isShowingSettings || isShowingLocations || isShowingWarranties) {
            isScanning = false
            isShowingStats = false
            isShowingSettings = false
            isShowingLocations = false
            isShowingWarranties = false
        }
    }
}
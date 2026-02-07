package pl.edu.ur.bw131534.homeinventory.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val locations by viewModel.locations.collectAsStateWithLifecycle(initialValue = emptyList())
    val items by viewModel.items.collectAsStateWithLifecycle(initialValue = emptyList())


    var showAddDialog by remember { mutableStateOf(false) }
    var locationToEdit by remember { mutableStateOf<LocationEntity?>(null) }
    var locationToDelete by remember { mutableStateOf<LocationEntity?>(null) }

    var tempName by remember { mutableStateOf("") }
    var tempFloor by remember { mutableStateOf("") }

    LaunchedEffect(locationToEdit) {
        locationToEdit?.let {
            tempName = it.name
            tempFloor = it.floor
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Twoje Pomieszczenia", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showAddDialog || locationToEdit != null) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    locationToEdit = null
                },
                title = { Text(if (locationToEdit != null) "Edytuj pomieszczenie" else "Dodaj nowe") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Nazwa (np. Kuchnia)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempFloor,
                            onValueChange = { tempFloor = it },
                            label = { Text("Piętro/Poziom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                val loc = locationToEdit?.copy(name = tempName, floor = tempFloor)
                                    ?: LocationEntity(0, tempName, tempFloor)

                                if (locationToEdit != null) viewModel.updateLocation(loc)
                                else viewModel.addLocation(loc)

                                showAddDialog = false
                                locationToEdit = null
                                tempName = ""; tempFloor = ""
                            }
                        }
                    ) { Text("Zapisz") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        locationToEdit = null
                    }) { Text("Anuluj") }
                }
            )
        }

        // --- Dialog Usuwania ---
        if (locationToDelete != null) {
            AlertDialog(
                onDismissRequest = { locationToDelete = null },
                title = { Text("Usuń pomieszczenie") },
                text = { Text("Czy na pewno chcesz usunąć ${locationToDelete?.name}?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteLocation(locationToDelete!!)
                        locationToDelete = null
                    }) { Text("Usuń", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { locationToDelete = null }) { Text("Anuluj") }
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(locations) { location ->
                val itemCount = items.count { it.locationId == location.id }
                EnhancedLocationCard(
                    location = location,
                    itemCount = itemCount,
                    onClick = { viewModel.toggleLocationFilter(location.id)
                        onNavigateBack() },
                    onEdit = {
                        locationToEdit = location
                    },
                    onDelete = { locationToDelete = location }
                )
            }

            item {
                Card(
                    onClick = { tempName = ""; tempFloor = ""; showAddDialog = true },
                    modifier = Modifier.height(130.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedLocationCard(
    location: LocationEntity,
    itemCount: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val icon = when {
        location.name.contains("Kuchnia", ignoreCase = true) -> Icons.Default.Restaurant
        location.name.contains("Salon", ignoreCase = true) -> Icons.Default.Tv
        location.name.contains("Garaż", ignoreCase = true) -> Icons.Default.DirectionsCar
        location.name.contains("Sypialnia", ignoreCase = true) -> Icons.Default.Bed
        else -> Icons.Default.DoorSliding
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edytuj") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Usuń", color = Color.Red) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "$itemCount szt.",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(location.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                if (location.floor.isNotBlank()) {
                    Text(location.floor, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}
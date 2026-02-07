package pl.edu.ur.bw131534.homeinventory.presentation.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.LocationEntity
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.InventoryViewModel
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.SortType
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.WarrantyViewModel
import pl.edu.ur.bw131534.homeinventory.ui.theme.StatusActiveBg
import pl.edu.ur.bw131534.homeinventory.ui.theme.StatusActiveText
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLocationsClick: () -> Unit,
    onWarrantiesClick: () -> Unit,
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    warrantyViewModel: WarrantyViewModel = hiltViewModel()
) {

    val filteredItems by inventoryViewModel.filteredItems.collectAsStateWithLifecycle()
    val locations by inventoryViewModel.locations.collectAsStateWithLifecycle()
    val searchQuery by inventoryViewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSortType by inventoryViewModel.sortType.collectAsStateWithLifecycle()
    val selectedLocId by inventoryViewModel.selectedLocationId.collectAsStateWithLifecycle()
    val items by inventoryViewModel.items.collectAsStateWithLifecycle(initialValue = emptyList())


    val warranties by inventoryViewModel.expiringWarranties.collectAsStateWithLifecycle()


    val context = LocalContext.current


    val showImageSourceDialog = remember{mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }
    var targetItemForImage by remember { mutableStateOf<ItemEntity?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var itemToEdit by remember { mutableStateOf<ItemEntity?>(null) }





    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                Log.e("IMAGE_ERROR", "Nie udało się uzyskać uprawnień: ${e.message}")
            }

            targetItemForImage?.let { item ->
                inventoryViewModel.updateItemImage(item, it.toString())
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val currentItem = targetItemForImage
            val currentUri = tempPhotoUri
            if (currentItem != null && currentUri != null) {
                inventoryViewModel.updateItemImage(currentItem, currentUri.toString())
            }
        }
    }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        val file = File(directory, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri()
            tempPhotoUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Aparat nie zadziała bez uprawnień", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(warranties, items) {
        if (warranties.isNotEmpty() && items.isNotEmpty()) {
            warrantyViewModel.checkWarrantiesAndNotify(warranties, items)
        }
    }

    if (showImageSourceDialog.value) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog.value = false },
            title = { Text("Dodaj zdjęcie") },
            text = { Text("Skąd chcesz pobrać zdjęcie?") },
            confirmButton = {
                TextButton(onClick = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    showImageSourceDialog.value = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoCamera, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Aparat")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    photoPickerLauncher.launch("image/*")
                    showImageSourceDialog.value = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoLibrary, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galeria")
                    }
                }
            }
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(text = "Usuwanie") },
            text = { Text("Czy na pewno chcesz usunąć przedmiot: \"${itemToDelete?.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {

                        itemToDelete?.let { item ->
                            inventoryViewModel.deleteItem(item)
                            Toast.makeText(context, "Usunięto: ${item.name}", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                ) {
                    Text("Usuń", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Anuluj")
                }
            }
        )
    }

    if (itemToEdit != null) {
        var editedName by remember { mutableStateOf(itemToEdit!!.name) }
        var editedPrice by remember { mutableStateOf(itemToEdit!!.price?.toString() ?: "") }
        var editedDescription by remember { mutableStateOf(itemToEdit!!.description ?: "") }

        AlertDialog(
            onDismissRequest = { itemToEdit = null },
            title = { Text("Edytuj przedmiot") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nazwa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedPrice,
                        onValueChange = { editedPrice = it },
                        label = { Text("Cena") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Opis") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updated = itemToEdit!!.copy(
                        name = editedName,
                        price = editedPrice.toDoubleOrNull(),
                        description = editedDescription
                    )
                    inventoryViewModel.updateItem(updated)
                    itemToEdit = null
                }) {
                    Text("Zapisz")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToEdit = null }) { Text("Anuluj") }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mój Dom") },
                actions = {
                    IconButton(onClick = onStatsClick) { Icon(Icons.Default.Assessment, null, tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onLocationsClick) { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onWarrantiesClick) { Icon(Icons.Default.VerifiedUser, null, tint = MaterialTheme.colorScheme.secondary) }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onScanClick, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Skanuj")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { inventoryViewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Szukaj przedmiotów...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SortType.entries.forEach { type ->
                            item {
                                FilterChip(
                                    selected = currentSortType == type,
                                    onClick = { inventoryViewModel.onSortTypeChanged(type) },
                                    label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                )
                                }
                        }
                    }
                }
            }

            item {
                Text("Pomieszczenia", modifier = Modifier.padding(start = 16.dp, bottom = 8.dp), fontWeight = FontWeight.Bold)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        DashboardLocationCard(
                            location = LocationEntity(id = -1, name = "Wszystkie", floor = ""),
                            itemCount = items.size,
                            isSelected = selectedLocId == null,
                            hasExpiredItems = false,
                            onClick = { inventoryViewModel.toggleLocationFilter(null) }
                        )
                    }

                    items(locations, key = { it.id }) { location ->
                        val count = items.count { it.locationId == location.id }
                        val hasExpiredInRoom = items
                            .filter { it.locationId == location.id }
                            .any { item ->
                                val w = warranties.find { it.itemId == item.id }
                                isWarrantyExpired(w?.expiryDate)
                            }

                        DashboardLocationCard(
                            location = location,
                            itemCount = count,
                            isSelected = selectedLocId == location.id,
                            hasExpiredItems = hasExpiredInRoom,
                            onClick = { inventoryViewModel.toggleLocationFilter(location.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }


            item {
                Text("Przedmioty", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }

            items(filteredItems) { item ->
                val itemWarranty = warranties.find { it.itemId == item.id }
                InventoryItemCard(
                    item = item,
                    isExpired = isWarrantyExpired(itemWarranty?.expiryDate),
                    onDeleteClick = { itemToDelete = item },
                    onAddImageClick = { targetItemForImage = item; showImageSourceDialog.value = true },
                    onEditClick = { itemToEdit = item }
                )
            }
        }
    }
}


@Composable
fun InventoryItemCard(
    item: ItemEntity,
    isExpired: Boolean,
    onDeleteClick: () -> Unit,
    onAddImageClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val statusLabel = if (isExpired) "Nieaktywny" else "Aktywny"
    val statusBg = if (isExpired) Color.LightGray else StatusActiveBg
    val statusTextColor = if (isExpired) Color.DarkGray else StatusActiveText
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
             .graphicsLayer(alpha = if (isExpired) 0.8f else 1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onAddImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUri != null) {
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.AddAPhoto, null)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Dodano ${item.dateAdded}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = statusTextColor,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (item.price != null) "${item.price} PLN" else "---",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Column {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edytuj",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, null, tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun DashboardLocationCard(location: LocationEntity, itemCount: Int, isSelected: Boolean, hasExpiredItems: Boolean, onClick: () -> Unit) {
    val icon = when {
        location.name.contains("Kuchnia", ignoreCase = true) -> Icons.Default.Restaurant
        location.name.contains("Salon", ignoreCase = true) -> Icons.Default.Tv
        location.name.contains("Garaż", ignoreCase = true) -> Icons.Default.DirectionsCar
        location.name.contains("Sypialnia", ignoreCase = true) -> Icons.Default.Bed
        else -> Icons.Default.DoorSliding
    }

    Card(
        modifier = Modifier
            .size(width = 135.dp, height = 115.dp)
            .clickable { onClick() }
            .graphicsLayer(
                scaleX = if (isSelected) 1.05f else 1f,
                scaleY = if (isSelected) 1.05f else 1f
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(28.dp))
            Column {
                Text(text = location.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text(text = "$itemCount szt.", style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
            }
        }
    }
}

private fun isWarrantyExpired(expiryDateStr: String?): Boolean {
    if (expiryDateStr.isNullOrBlank()) return false
    return try {
        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        val expiry = sdf.parse(expiryDateStr)
        expiry != null && System.currentTimeMillis() > expiry.time
    } catch (e: Exception) {
        false
    }
}
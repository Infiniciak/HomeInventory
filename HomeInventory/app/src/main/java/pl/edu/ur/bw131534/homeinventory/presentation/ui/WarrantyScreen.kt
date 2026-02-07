package pl.edu.ur.bw131534.homeinventory.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.InventoryViewModel
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.WarrantyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyScreen(
    onNavigateBack: () -> Unit,
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    warrantyViewModel: WarrantyViewModel = hiltViewModel()
) {
    val allWarranties by warrantyViewModel.expiringWarranties.collectAsState()
    val allItems by inventoryViewModel.items.collectAsState(initial = emptyList())

    var editingWarranty by remember { mutableStateOf<WarrantyEntity?>(null) }
    var warrantyToDelete by remember { mutableStateOf<WarrantyEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allWarranties, allItems) {
        if (allWarranties.isNotEmpty() && allItems.isNotEmpty()) {
            warrantyViewModel.checkWarrantiesAndNotify(allWarranties, allItems)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gwarancje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj gwarancję")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (allWarranties.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak aktywnych gwarancji", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(allWarranties) { warranty ->
                        val relatedItem = allItems.find { it.id == warranty.itemId }
                        WarrantyItemCard(
                            warranty = warranty,
                            item = relatedItem,
                            onClick = { editingWarranty = warranty },
                            onDelete = { warrantyToDelete = warranty }
                        )
                    }
                }
            }

            // Dialog potwierdzenia usuwania
            if (warrantyToDelete != null) {
                AlertDialog(
                    onDismissRequest = { warrantyToDelete = null },
                    title = { Text("Usuń gwarancję") },
                    text = { Text("Czy na pewno chcesz usunąć informację o gwarancji dla tego przedmiotu?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                warrantyViewModel.deleteWarranty(warrantyToDelete!!)
                                warrantyToDelete = null
                            }
                        ) { Text("Usuń", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { warrantyToDelete = null }) { Text("Anuluj") }
                    }
                )
            }

            // Dialog dodawania/edycji
            if (showAddDialog || editingWarranty != null) {
                AddWarrantyDialog(
                    items = allItems,
                    editWarranty = editingWarranty,
                    onDismiss = {
                        showAddDialog = false
                        editingWarranty = null
                    },
                    onConfirm = { itemId, date, prov ->
                        if (editingWarranty != null) {
                            warrantyViewModel.saveWarranty(
                                editingWarranty!!.copy(expiryDate = date, provider = prov)
                            )
                        } else {
                            val alreadyExists = allWarranties.any { it.itemId == itemId }
                            if (!alreadyExists) {
                                warrantyViewModel.saveWarranty(
                                    WarrantyEntity(
                                        itemId = itemId,
                                        expiryDate = date,
                                        provider = prov,
                                        notes = "Dodano ręcznie"
                                    )
                                )
                            }
                        }
                        showAddDialog = false
                        editingWarranty = null
                    }
                )
            }
        }
    }
}

// --- Komponenty UI wyciągnięte na zewnątrz ---

@Composable
fun WarrantyItemCard(
    warranty: WarrantyEntity,
    item: ItemEntity?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val expiringSoon = isExpiringSoon(warranty.expiryDate)
    val progress = calculateRemainingProgress(warranty.expiryDate)

    val statusColor = when {
        expiringSoon -> Color.Red
        progress > 0.85f -> Color(0xFFFFA500)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item?.imageUri != null) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VerifiedUser, null, tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item?.name ?: "Nieznany przedmiot", fontWeight = FontWeight.Bold)
                Text(
                    text = "Gwarant: ${warranty.provider ?: "Brak danych"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.2f)
                )

                Text(
                    text = "Wygasa: ${warranty.expiryDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (progress > 0.8f) Color.Red else Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Usuń", tint = Color.Gray.copy(alpha = 0.6f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWarrantyDialog(
    items: List<ItemEntity>,
    editWarranty: WarrantyEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String) -> Unit
) {
    var selectedItem by remember { mutableStateOf(items.find { it.id == editWarranty?.itemId }) }
    var expiryDate by remember { mutableStateOf(editWarranty?.expiryDate ?: "") }
    var provider by remember { mutableStateOf(editWarranty?.provider ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editWarranty == null) "Dodaj gwarancję" else "Edytuj gwarancję") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box {
                    OutlinedTextField(
                        value = selectedItem?.name ?: "Wybierz przedmiot",
                        onValueChange = {},
                        readOnly = true,
                        enabled = editWarranty == null,
                        label = { Text("Przedmiot") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { if (editWarranty == null) ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    if (editWarranty == null) {
                        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        items.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.name} (${item.modelId})") },
                                onClick = { selectedItem = item; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Data wygaśnięcia (DD.MM.RRRR)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text("Gwarant / Sklep") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedItem?.let { onConfirm(it.id, expiryDate, provider) } },
                enabled = selectedItem != null && expiryDate.length >= 10
            ) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}

// --- Funkcje logiczne ---

fun isExpiringSoon(dateStr: String): Boolean {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateStr)
        date?.before(Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)) ?: false
    } catch (e: Exception) { false }
}

fun calculateRemainingProgress(expiryDateStr: String): Float {
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val expiry = sdf.parse(expiryDateStr)?.time ?: return 0f
        val now = System.currentTimeMillis()
        val totalDurationMs = 2L * 365 * 24 * 60 * 60 * 1000
        val startDate = expiry - totalDurationMs
        ((now - startDate).toFloat() / (expiry - startDate).toFloat()).coerceIn(0f, 1f)
    } catch (e: Exception) { 0f }
}
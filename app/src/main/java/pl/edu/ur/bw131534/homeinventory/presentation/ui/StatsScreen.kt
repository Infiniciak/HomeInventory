package pl.edu.ur.bw131534.homeinventory.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.edu.ur.bw131534.homeinventory.data.local.entity.CategoryEntity
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.categoryStats.collectAsState()
    val totalCount = stats.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statystyki", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wszystkie przedmioty", style = MaterialTheme.typography.titleMedium)
                        Text("$totalCount", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
                    }
                }
            }


            if (totalCount > 0) {
                item {
                    CategoryPieChart(stats)
                }
            }


            items(stats.toList()) { (category, count) ->
                ListItem(
                    headlineContent = { Text(category.name) },
                    supportingContent = { Text("${(count.toFloat()/totalCount*100).toInt()}% udziału") },
                    trailingContent = {
                        Badge { Text("$count", modifier = Modifier.padding(4.dp)) }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
            }
        }
    }
}



@Composable
fun CategoryPieChart(stats: Map<CategoryEntity, Int>) {
    val totalItems = stats.values.sum()
    if (totalItems == 0) return


    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        Color(0xFF4CAF50),
        Color(0xFFFFC107)
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = -90f
            stats.toList().forEachIndexed { index, pair ->
                val sweepAngle = (pair.second.toFloat() / totalItems) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            stats.toList().forEachIndexed { index, (category, _) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                    Box(modifier = Modifier.size(12.dp).background(colors[index % colors.size], CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(category.name, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
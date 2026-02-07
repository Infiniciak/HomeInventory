package pl.edu.ur.bw131534.homeinventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.edu.ur.bw131534.homeinventory.data.local.entity.ItemEntity
import pl.edu.ur.bw131534.homeinventory.data.local.entity.WarrantyEntity
import pl.edu.ur.bw131534.homeinventory.domain.repository.WarrantyRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class WarrantyViewModel @Inject constructor(
    private val warrantyRepository: WarrantyRepository,
    private val application: android.app.Application
) : ViewModel() {

    private val CHANNEL_ID = "warranty_notifications"

    val expiringWarranties = warrantyRepository.getAllWarranties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun checkWarrantiesAndNotify(warranties: List<WarrantyEntity>, items: List<ItemEntity>) {
        createNotificationChannel()
        warranties.forEach { warranty ->
            val days = getDaysRemaining(warranty.expiryDate)
            val itemName = items.find { it.id == warranty.itemId }?.name ?: "Przedmiot"

            val message = when {
                days < 0L -> "Gwarancja już wygasła!"
                days == 0L -> "Gwarancja wygasa DZISIAJ!"
                days == 1L -> "Gwarancja wygasa jutro!"
                days in 2..7 -> "Gwarancja wygasa za $days dni."
                days in 8..30 -> "Gwarancja wygasa za około ${days / 7} tyg."
                days in 31..365 -> "Gwarancja wygasa za około ${days / 30} mies."
                days > 365 -> "Gwarancja wygasa za ponad rok (${days / 365} lat/a)."
                else -> null
            }

            if (message != null && days <= 30) {
                sendWarrantyNotification(itemName, message)
            }
        }
    }

    private fun getDaysRemaining(expiryDateString: String): Long {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            val expiryDate = sdf.parse(expiryDateString) ?: return 0L
            val diff = expiryDate.time - System.currentTimeMillis()
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: Exception) { 0L }
    }

    private fun createNotificationChannel() {
        val name = "Powiadomienia o gwarancjach"
        val descriptionText = "Informuje o zbliżającym się końcu gwarancji przedmiotów"
        val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
        val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = application.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun sendWarrantyNotification(itemName: String, message: String) {
        val notificationManager = application.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val builder = androidx.core.app.NotificationCompat.Builder(application, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(itemName)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(itemName.hashCode(), builder.build())
    }

    fun saveWarranty(warranty: WarrantyEntity) {
        viewModelScope.launch {
            warrantyRepository.saveWarranty(warranty)
        }
    }


    fun deleteWarranty(warranty: WarrantyEntity) {
        viewModelScope.launch {
            warrantyRepository.deleteWarranty(warranty) // Nazwa metody zależy od Twojego Repository/DAO
        }
    }
}

package pl.edu.ur.bw131534.homeinventory.worker // lub Twój pakiet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.edu.ur.bw131534.homeinventory.presentation.ui.HomeInventoryActivity
import pl.edu.ur.bw131534.homeinventory.R

class InventoryNotification(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val context = applicationContext
        val channelId = "inventory_review_channel"
        val notificationId = 101

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Tworzenie kanału powiadomień (Wymagane dla Androida 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Przypomnienia o przeglądzie",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Przypomina o okresowym przeglądzie inwentarza"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Co ma się stać po kliknięciu w powiadomienie (Otwórz aplikację)
        val intent = Intent(context, HomeInventoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Budowanie powiadomienia
        // Upewnij się, że masz ikonę 'ic_launcher_foreground' lub inną w res/drawable
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Zmień na swoją ikonę, np. R.drawable.ic_inventory
            .setContentTitle("Czas na przegląd domu! 🏠")
            .setContentText("Minęło trochę czasu. Sprawdź, czy Twój inwentarz jest aktualny.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4. Wyświetlenie
        // Sprawdzenie uprawnień wewnątrz Workera jest trudne, zakładamy że użytkownik je nadał w UI
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Brak uprawnień do powiadomień (Android 13+)
            e.printStackTrace()
        }
    }
}
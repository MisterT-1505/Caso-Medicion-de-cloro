package com.example.cloromonitoreo.data.local

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Locale
import kotlin.random.Random

class NotificacionHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "canal_alertas_cloro"
    }

    init {
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Alertas Críticas de Cloro",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones inmediatas cuando el cloro sale del rango crítico (<2.00 o >6.00 ppm)"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    fun enviarAlertaCritica(puntoId: String, granja: String, galpon: String, nivelPpm: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val tipoDesviacion = if (nivelPpm < 2.00) "Cloro insuficiente (< 2.00 ppm)" else "Cloro excesivo (> 6.00 ppm)"
        val nivelFormateado = String.format(Locale.US, "%.2f", nivelPpm)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("¡Alerta Crítica en $granja!")
            .setContentText("$puntoId ($galpon): $nivelFormateado ppm - $tipoDesviacion")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(Random.nextInt(1000, 9999), builder.build())
    }
}
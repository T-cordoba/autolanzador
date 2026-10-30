package co.tomas.autolanzador

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

/**
 * Servicio en primer plano que se queda escuchando cuando la pantalla se enciende
 * (salida de suspensión). SCREEN_ON solo llega a receptores registrados en código,
 * por eso hace falta un servicio vivo; un receptor del manifiesto no lo recibe.
 */
class WatchService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val launchRunnable = Runnable { Launcher.launchTarget(this) }
    private var receiverRegistered = false

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                scheduleLaunch(Config.WAKE_DELAY_MS)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startAsForeground()

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenReceiver, filter)
        }
        receiverRegistered = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val delay = intent?.getLongExtra(EXTRA_LAUNCH_AFTER_MS, 0L) ?: 0L
        if (delay > 0L) scheduleLaunch(delay)
        return START_STICKY
    }

    private fun scheduleLaunch(delayMs: Long) {
        handler.removeCallbacks(launchRunnable)
        handler.postDelayed(launchRunnable, delayMs)
    }

    private fun startAsForeground() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "AutoLanzador", NotificationManager.IMPORTANCE_MIN)
        )

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("AutoLanzador activo")
            .setContentText("Abrirá ${Config.TARGET_PACKAGE} al encender")
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(launchRunnable)
        if (receiverRegistered) unregisterReceiver(screenReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "watch"
        private const val NOTIFICATION_ID = 1
        private const val EXTRA_LAUNCH_AFTER_MS = "launch_after_ms"

        fun start(context: Context, launchAfterMs: Long = 0L) {
            val intent = Intent(context, WatchService::class.java)
                .putExtra(EXTRA_LAUNCH_AFTER_MS, launchAfterMs)
            context.startForegroundService(intent)
        }
    }
}

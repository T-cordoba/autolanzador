package co.tomas.autolanzador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isBoot = intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"

        // Al arrancar: inicia el vigilante y abre la app.
        // Al actualizar esta app: solo reinicia el vigilante, sin abrir nada.
        WatchService.start(context, launchAfterMs = if (isBoot) Config.BOOT_DELAY_MS else 0L)
    }
}

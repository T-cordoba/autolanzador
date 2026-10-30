package co.tomas.autolanzador

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log

object Launcher {
    private const val TAG = "AutoLanzador"

    fun isTargetInstalled(context: Context): Boolean = try {
        context.packageManager.getPackageInfo(Config.TARGET_PACKAGE, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    fun hasOverlayPermission(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun launchTarget(context: Context): Boolean {
        val pm = context.packageManager
        // En TV primero se busca la entrada "leanback"; si no existe, la normal.
        val intent = pm.getLeanbackLaunchIntentForPackage(Config.TARGET_PACKAGE)
            ?: pm.getLaunchIntentForPackage(Config.TARGET_PACKAGE)

        if (intent == null) {
            Log.w(TAG, "No se encontró ${Config.TARGET_PACKAGE} o no tiene actividad de inicio")
            return false
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        return try {
            context.startActivity(intent)
            Log.i(TAG, "Abriendo ${Config.TARGET_PACKAGE}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Android bloqueó el arranque de ${Config.TARGET_PACKAGE}", e)
            false
        }
    }
}

package co.tomas.autolanzador

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** Pantalla mínima, sin librerías extra, pensada para manejarse con el control remoto. */
class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (32 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.parseColor("#101418"))
        }

        root.addView(TextView(this).apply {
            text = "AutoLanzador"
            textSize = 28f
            setTextColor(Color.WHITE)
        })

        status = TextView(this).apply {
            textSize = 18f
            setTextColor(Color.parseColor("#C8D0D8"))
            setPadding(0, pad / 2, 0, pad / 2)
        }
        root.addView(status)

        val permissionButton = Button(this).apply {
            text = "1. Dar permiso \"Mostrar sobre otras apps\""
            setOnClickListener { openOverlaySettings() }
        }
        root.addView(permissionButton)

        root.addView(Button(this).apply {
            text = "2. Probar: abrir la app ahora"
            setOnClickListener {
                if (!Launcher.launchTarget(this@MainActivity)) {
                    Toast.makeText(this@MainActivity, "No se pudo abrir ${Config.TARGET_PACKAGE}", Toast.LENGTH_LONG).show()
                }
            }
        })

        setContentView(root)
        permissionButton.requestFocus()

        // Deja el vigilante corriendo desde ya, sin esperar a un reinicio.
        WatchService.start(this)
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        val installed = Launcher.isTargetInstalled(this)
        val overlay = Launcher.hasOverlayPermission(this)
        status.text = buildString {
            append(if (installed) "✔" else "✘").append(" App objetivo instalada (${Config.TARGET_PACKAGE})\n")
            append(if (overlay) "✔" else "✘").append(" Permiso \"Mostrar sobre otras apps\"\n")
            append(if (installed && overlay) "\nListo. Reinicia el box para probar." else "\nFalta configurar lo marcado con ✘.")
        }
    }

    private fun openOverlaySettings() {
        val attempts = listOf(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
            Intent(Settings.ACTION_SETTINGS),
        )
        for (intent in attempts) {
            try {
                startActivity(intent)
                return
            } catch (_: ActivityNotFoundException) {
                // Algunos TV box no traen esa pantalla; se prueba la siguiente.
            }
        }
    }
}

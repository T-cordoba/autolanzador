package co.tomas.autolanzador

object Config {
    /** App que se abre al encender o despertar el box. Si la cambias, cámbiala también en <queries> del AndroidManifest. */
    const val TARGET_PACKAGE = "com.xuper.netxxus"

    /** Espera tras el arranque, para que el launcher del sistema no tape la app. */
    const val BOOT_DELAY_MS = 8_000L

    /** Espera tras salir de suspensión. */
    const val WAKE_DELAY_MS = 2_000L
}

# AutoLanzador

Abre `com.xuper.netxxus` cuando el TV box enciende o sale de suspensión (Android 8 a 14, sin root).

## Compilar con GitHub
1. Crea un repo **público** y sube esta carpeta a la rama `main`.
2. La pestaña **Actions** compila solo; al terminar crea una release con `autolanzador.apk`.

## Instalar en el box con Downloader
Escribe en Downloader:

    github.com/TU_USUARIO/TU_REPO/releases/latest/download/autolanzador.apk

## Configurar
1. Abre **AutoLanzador** desde la lista de apps.
2. Botón 1 → activa "Mostrar sobre otras apps" (ahora sí aparece en la lista).
3. Botón 2 → prueba que abra la app.
4. Reinicia el box.

## Cambiar la app objetivo
Edita `Config.TARGET_PACKAGE` y el `<package>` dentro de `<queries>` en `AndroidManifest.xml`.

## Actualizar
Cada compilación de GitHub usa una llave de firma distinta: desinstala la versión anterior antes de instalar la nueva.

# Liga Diamante para Android

App Android (Kotlin) que empaqueta el juego (`app/src/main/assets/www/index.html`) dentro de un WebView. Funciona sin internet.

> **Aviso:** este proyecto se escribió sin poder compilarlo ni ejecutarlo en un dispositivo. Los archivos XML se validaron como XML bien formado y el código Kotlin sigue las APIs estándar de Android, pero la primera compilación puede pedir algún ajuste de versiones. Si algo falla, copia el mensaje de error y se corrige.

## Requisitos
- JDK 17
- Android SDK 34 (se instala con Android Studio)
- Android 8.0 (API 26) o superior en el teléfono

## Opción A: Android Studio (la más fácil)
1. Instala Android Studio.
2. `File > Open` y elige esta carpeta (`liga-diamante-android`).
3. Espera a que termine "Gradle Sync". Si pide crear el wrapper de Gradle, acepta (usa Gradle 8.7).
4. Conecta tu teléfono con la depuración USB activada y pulsa **Run**.
5. Para un APK: `Build > Build Bundle(s) / APK(s) > Build APK(s)`. El archivo queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Opción B: sin instalar nada (GitHub Actions)
1. Sube esta carpeta a un repositorio nuevo de GitHub (rama `main`).
2. Abre la pestaña **Actions**. El flujo **Compilar APK** se ejecuta solo (o pulsa *Run workflow*).
3. Al terminar, descarga el artefacto **liga-diamante-debug-apk** y extrae el `.apk`.
4. Instálalo en el teléfono (hay que permitir "instalar apps desconocidas").

## Opción C: línea de comandos
Con Gradle 8.7 instalado y `ANDROID_HOME` configurado:
```
gradle assembleDebug
```

## APK firmado para publicar
Un APK de depuración sirve para instalar en tu teléfono. Para publicar en una tienda necesitas firmarlo:
1. Crea una clave: `keytool -genkey -v -keystore liga.jks -keyalg RSA -keysize 2048 -validity 10000 -alias liga`
2. Añade un `signingConfigs` a `app/build.gradle.kts` (o firma el APK de `assembleRelease` con `apksigner`).
3. Guarda la clave en un lugar seguro: sin ella no podrás actualizar la app.

## Actualizar el juego
Reemplaza `app/src/main/assets/www/index.html` por una versión nueva de la página y vuelve a compilar.

## Cómo funciona
- **Partida guardada:** el juego usa `localStorage`; el WebView lo conserva entre sesiones. Si desinstalas la app, se borra.
- **Exportar archivo:** el juego llama a `AndroidBridge.save(nombre, texto)` y Android abre el selector "Guardar como".
- **Importar:** el selector de archivos del sistema se abre desde el botón de importar.
- **Botón atrás:** cierra la app.

## Límites conocidos
- No compilado ni probado en un dispositivo real.
- El tema oscuro del juego depende de la configuración del WebView de cada teléfono.
- Solo hay un icono sencillo (balón de béisbol blanco sobre verde).

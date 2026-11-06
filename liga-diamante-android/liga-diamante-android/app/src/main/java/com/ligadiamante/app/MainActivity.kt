package com.ligadiamante.app

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * Liga Diamante: envuelve la pagina del juego (assets/www/index.html) en un WebView.
 * - La partida se guarda en el almacenamiento local del WebView (localStorage).
 * - "Exportar" usa el puente AndroidBridge.save para elegir donde guardar el .json.
 * - "Importar" usa el selector de archivos del sistema.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView
    private var chooserCallback: ValueCallback<Array<Uri>>? = null
    private var pendingExport: String? = null

    private val pickFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            chooserCallback?.onReceiveValue(if (uri != null) arrayOf(uri) else null)
            chooserCallback = null
        }

    private val createDocument =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            val data = pendingExport
            pendingExport = null
            if (uri != null && data != null) {
                try {
                    contentResolver.openOutputStream(uri)?.use { it.write(data.toByteArray(Charsets.UTF_8)) }
                } catch (e: Exception) {
                    // Si falla el guardado, la pagina sigue ofreciendo copiar el texto.
                }
            }
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setContentView(web)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
        }
        web.addJavascriptInterface(Bridge(), "AndroidBridge")
        web.webViewClient = WebViewClient()
        web.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {
                chooserCallback?.onReceiveValue(null)
                chooserCallback = callback
                pickFile.launch("*/*")
                return true
            }
        }

        if (savedInstanceState == null) {
            web.loadUrl("file:///android_asset/www/index.html")
        } else {
            web.restoreState(savedInstanceState)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    inner class Bridge {
        @JavascriptInterface
        fun save(name: String, data: String) {
            runOnUiThread {
                pendingExport = data
                createDocument.launch(name)
            }
        }
    }
}

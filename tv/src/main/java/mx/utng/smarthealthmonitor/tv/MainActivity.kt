// tv/src/main/java/mx/utng/smarthealthmonitor/tv/MainActivity.kt
package mx.utng.smarthealthmonitor.tv

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.maai.data.MqttHelper
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository

/**
 * MainActivity para Android TV.
 * Es solo el contenedor: carga MainFragment.
 * TODA la lógica de UI va en el Fragment (patrón Leanback).
 *
 * Además conecta al broker MQTT y enruta los mensajes recibidos:
 *  - /fc      → guarda la FC en el repository (hilo IO, toca Room)
 *  - /pasos   → guarda los pasos en el repository (hilo IO, toca Room)
 *  - /alertas → muestra un Toast (hilo Main, es UI)
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            MqttHelper.connect { topic, message ->
                when {
                    topic.endsWith("/fc") -> {
                        val bpm = message.toIntOrNull() ?: 0
                        // Room NUNCA en el hilo Main → Dispatchers.IO
                        CoroutineScope(Dispatchers.IO).launch {
                            SmartHealthRepository.actualizarFC(bpm)
                        }
                    }
                    topic.endsWith("/pasos") -> {
                        val pasos = message.toIntOrNull() ?: 0
                        // Igual que FC: a IO para no crashear si toca Room
                        CoroutineScope(Dispatchers.IO).launch {
                            SmartHealthRepository.actualizarPasos(pasos)
                        }
                    }
                    topic.endsWith("/alertas") -> {
                        // El Toast sí es UI → hilo Main
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        // Solo agregar el fragment si es la primera creación
        // (evita duplicarlo en recreaciones de Activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MqttHelper.disconnect()
    }
}
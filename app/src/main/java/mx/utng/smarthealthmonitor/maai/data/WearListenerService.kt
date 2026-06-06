package mx.utng.smarthealthmonitor.maai.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WearListenerService : WearableListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val PATH_FC    = "/smarthealthmonitor/fc"
        const val PATH_PASOS = "/smarthealthmonitor/pasos"
        private const val TAG = "WearListener"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        val data = String(messageEvent.data)
        val path = messageEvent.path
        Log.i(TAG, "✅ Mensaje recibido desde reloj: path=$path, data=$data")

        when (path) {
            PATH_FC -> {
                val bpm = data.toIntOrNull() ?: run {
                    Log.e(TAG, "❌ BPM inválido: $data")
                    return
                }
                Log.i(TAG, "❤️ BPM actualizado: $bpm")
                scope.launch { SmartHealthRepository.actualizarFC(bpm) }
            }
            PATH_PASOS -> {
                val pasos = data.toIntOrNull() ?: run {
                    Log.e(TAG, "❌ Pasos inválido: $data")
                    return
                }
                Log.i(TAG, "👟 Pasos actualizados: $pasos")
                SmartHealthRepository.actualizarPasos(pasos)
            }
            else -> Log.w(TAG, "⚠️ Path desconocido: $path")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
package mx.utng.smarthealthmonitor.maai.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService : WearableListenerService() {

    companion object {
        const val PATH_FC    = "/smarthealthmonitor/fc"
        const val PATH_PASOS = "/smarthealthmonitor/pasos"
        private const val TAG = "WearListener"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = String(messageEvent.data)
        val path = messageEvent.path
        Log.d(TAG, "Mensaje recibido: path=$path, data=$data")

        when (path) {
            PATH_FC -> {
                val bpm = data.toIntOrNull() ?: return
                SmartHealthRepository.actualizarFC(bpm)
            }
            PATH_PASOS -> {
                val pasos = data.toIntOrNull() ?: return
                SmartHealthRepository.actualizarPasos(pasos)
            }
            else -> Log.w(TAG, "Path desconocido: $path")
        }
    }
}
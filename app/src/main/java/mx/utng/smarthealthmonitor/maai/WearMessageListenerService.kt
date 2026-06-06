package mx.utng.smarthealthmonitor.maai

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearMessageListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val dataPath = messageEvent.path
        val dataString = String(messageEvent.data)

        Log.d("CelularWearListener", "Mensaje recibido en path: $dataPath con valor: $dataString")

        when (dataPath) {
            "/smarthealthmonitor/fc" -> {
                // Aquí procesas los BPM
                Log.d("CelularWearListener", "❤️ BPM del reloj: $dataString")
            }
            "/smarthealthmonitor/pasos" -> {
                // Aquí procesas los pasos
                Log.d("CelularWearListener", "👟 Pasos del reloj: $dataString")
            }
        }
    }
}
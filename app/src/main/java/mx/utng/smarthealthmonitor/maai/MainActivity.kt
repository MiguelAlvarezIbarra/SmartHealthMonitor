package mx.utng.smarthealthmonitor.maai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.maai.navigation.SmartHealthNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        CoroutineScope(Dispatchers.IO).launch {
            mx.utng.smarthealthmonitor.maai.data.MqttHelper.connect { topic, message ->
                // En la app no necesitamos hacer nada al recibir (pues nosotros enviamos), 
                // pero podríamos actualizar el UI si quisiéramos.
            }
        }

        setContent {
            SmartHealthNavGraph()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mx.utng.smarthealthmonitor.maai.data.MqttHelper.disconnect()
    }
}
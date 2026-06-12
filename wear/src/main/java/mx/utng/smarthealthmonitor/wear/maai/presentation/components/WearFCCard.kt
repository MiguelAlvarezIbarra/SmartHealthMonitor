package mx.utng.smarthealthmonitor.wear.maai.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun WearFCCard(
    fc: Int,
    modifier: Modifier = Modifier
) {
    val colorFC = when {
        fc == 0          -> MaterialTheme.colors.onSurface   // Sin lectura: color neutro
        fc in 60..100    -> MaterialTheme.colors.primary     // Normal: verde/primario
        else             -> MaterialTheme.colors.error       // Anormal: rojo
    }

    Card(
        onClick = { },
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❤",
                fontSize = 20.sp,
                color = colorFC
            )
            Text(
                text = if (fc == 0) "--" else "$fc",
                style = MaterialTheme.typography.display3,
                color = colorFC
            )
            Text(
                text = "bpm",
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurfaceVariant
            )
        }
    }
}
// tv/src/main/java/mx/utng/smarthealthmonitor/tv/FCCardPresenter.kt
package mx.utng.smarthealthmonitor.tv

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import mx.utng.smarthealthmonitor.maai.data.db.LecturaFC

/**
 * Presenter que convierte una [LecturaFC] en una [ImageCardView] de Leanback.
 *
 * Leanback usa el patrón Presenter (similar a RecyclerView.Adapter) para
 * renderizar items en las filas del BrowseSupportFragment.
 *
 * Colores:
 *  - FC normal (60-100 bpm) → Azul primario (#1B4F8A)
 *  - FC fuera de rango       → Rojo error  (#B3261E)
 *
 * Caso especial: la card de pasos reutiliza LecturaFC como contenedor
 * (hora == "Pasos") y se muestra con formato "N pasos" en vez de "N bpm".
 */
class FCCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            // CRÍTICO: sin isFocusable el D-pad no puede navegar a este card
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(240, 180)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val card    = viewHolder.view as ImageCardView
        val lectura = item as LecturaFC

        // La card de pasos viene marcada con hora == "Pasos"
        val esCardPasos = lectura.hora == "Pasos"

        if (esCardPasos) {
            card.titleText   = "%,d pasos".format(lectura.valorBpm)
            card.contentText = "Hoy"
        } else {
            card.titleText   = "${lectura.valorBpm} bpm"
            card.contentText = lectura.hora
        }

        // Color de fondo según si la FC está en rango normal (60-100 bpm)
        val bgColor = if (lectura.esNormal) {
            Color.parseColor("#1B4F8A")   // Azul primario
        } else {
            Color.parseColor("#B3261E")   // Rojo error
        }
        card.setBackgroundColor(bgColor)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Liberar imagen para evitar memory leaks al hacer scroll
        (viewHolder.view as ImageCardView).mainImage = null
    }
}
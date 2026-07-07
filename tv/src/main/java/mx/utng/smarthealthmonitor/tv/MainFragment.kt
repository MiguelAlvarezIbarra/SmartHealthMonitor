// tv/src/main/java/mx/utng/smarthealthmonitor/tv/MainFragment.kt
package mx.utng.smarthealthmonitor.tv

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.maai.data.db.LecturaFC

/**
 * Fragment principal de la app Android TV.
 *
 * Usa [BrowseSupportFragment] de Leanback que provee:
 *  - Sidebar con headers navegables
 *  - Filas horizontales de cards
 *  - Navegación D-pad automática
 *
 * Arquitectura:
 *  - Fila 1: "Estado actual"  → FC en tiempo real + pasos (datos del ViewModel)
 *  - Fila 2: "Historial FC"   → Lecturas de Room (ReactiveFlow del ViewModel)
 *  - Fila 3: "Alertas"        → Lecturas fuera de rango
 */
class MainFragment : BrowseSupportFragment() {

    private val viewModel: TvViewModel by viewModels()

    // Adapters de cada fila — se mantienen como propiedades para poder
    // actualizarlos cuando lleguen nuevos datos del Flow
    private lateinit var estadoAdapter: ArrayObjectAdapter
    private lateinit var histAdapter: ArrayObjectAdapter
    private lateinit var alertasAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Configuración del BrowseSupportFragment ──────────────────────
        title        = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Color de la marca en el sidebar
        brandColor = resources.getColor(R.color.sh_primary, null)

        configurarFilas()
        observarDatos()
    }

    /**
     * Construye la estructura de filas y la registra en el BrowseSupportFragment.
     * Los adapters quedan vacíos; [observarDatos] los llena con datos reales.
     */
    private fun configurarFilas() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // ── Fila 1: Estado actual ─────────────────────────────────────────
        estadoAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(0, "Estado actual"), estadoAdapter))

        // ── Fila 2: Historial FC ──────────────────────────────────────────
        histAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(1, "Historial FC"), histAdapter))

        // ── Fila 3: Alertas recientes (FC fuera de rango) ────────────────
        alertasAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(2, "Alertas recientes"), alertasAdapter))

        this.adapter = rowsAdapter
    }

    /**
     * Observa los StateFlows del [TvViewModel] y actualiza los adapters
     * de forma reactiva cuando cambian los datos de Room o del wearable.
     */
    private fun observarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // ── FC actual del wearable → Fila "Estado actual" ────────
                launch {
                    viewModel.fc.collect { bpm ->
                        estadoAdapter.clear()
                        // Card de frecuencia cardíaca actual
                        estadoAdapter.add(LecturaFC(valorBpm = bpm, hora = "Ahora"))
                        // Card de pasos (valor actual del repository)
                        estadoAdapter.add(
                            LecturaFC(
                                valorBpm = viewModel.pasos.value,
                                hora     = "Pasos",
                                esNormal = true
                            )
                        )
                    }
                }

                // ── Historial Room → Fila "Historial FC" ─────────────────
                launch {
                    viewModel.historial.collect { lecturas ->
                        histAdapter.clear()
                        lecturas.forEach { histAdapter.add(it) }
                    }
                }

                // ── Alertas (FC > 100 o < 60) → Fila "Alertas recientes" ─
                launch {
                    viewModel.historial.collect { lecturas ->
                        alertasAdapter.clear()
                        lecturas.filter { !it.esNormal }
                            .forEach { alertasAdapter.add(it) }
                    }
                }
            }
        }
    }
}

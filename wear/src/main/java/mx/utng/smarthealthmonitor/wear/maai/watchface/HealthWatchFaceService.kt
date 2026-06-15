package mx.utng.smarthealthmonitor.wear.maai.watchface

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository
import java.time.ZonedDateTime

private const val FRAME_PERIOD_MS = 1000L  // 1 fps en modo interactivo (reloj digital)

/**
 * WatchFace básico — Sesión 10
 *
 * Muestra:
 *  - Hora digital centrada (HH:MM)
 *  - Segundos en tamaño pequeño
 *  - FC actual del repositorio (❤ XX bpm)
 */
class HealthWatchFaceService : WatchFaceService() {

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = HealthRenderer(
            surfaceHolder = surfaceHolder,
            currentUserStyleRepository = currentUserStyleRepository,
            watchState = watchState
        )
        return WatchFace(WatchFaceType.DIGITAL, renderer)
    }

    // -----------------------------------------------------------------------
    // Renderer
    // -----------------------------------------------------------------------
    inner class HealthRenderer(
        surfaceHolder: SurfaceHolder,
        currentUserStyleRepository: CurrentUserStyleRepository,
        watchState: WatchState
    ) : Renderer.CanvasRenderer2<Renderer.SharedAssets>(
        surfaceHolder = surfaceHolder,
        currentUserStyleRepository = currentUserStyleRepository,
        watchState = watchState,
        canvasType = CanvasType.HARDWARE,
        interactiveDrawModeUpdateDelayMillis = FRAME_PERIOD_MS,
        clearWithBackgroundTintBeforeRenderingHighlightLayer = true
    ) {

        // Pintura para la hora (grande, blanco)
        private val timePaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Pintura para los segundos (gris claro)
        private val secondsPaint = Paint().apply {
            color = Color.LTGRAY
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Pintura para la FC (rojo, corazón)
        private val fcPaint = Paint().apply {
            color = Color.RED
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Pintura para la FC cuando no hay lectura (gris)
        private val fcNoDataPaint = Paint().apply {
            color = Color.DKGRAY
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        override suspend fun createSharedAssets(): Renderer.SharedAssets =
            object : Renderer.SharedAssets {
                override fun onDestroy() { /* sin assets externos */ }
            }

        override fun render(
            canvas: Canvas,
            bounds: Rect,
            zonedDateTime: ZonedDateTime,
            sharedAssets: Renderer.SharedAssets
        ) {
            val cx = bounds.exactCenterX()
            val cy = bounds.exactCenterY()
            val w = bounds.width().toFloat()

            // — Fondo negro —
            canvas.drawColor(Color.BLACK)

            // — Hora: HH:MM —
            val timeStr = String.format("%02d:%02d", zonedDateTime.hour, zonedDateTime.minute)
            timePaint.textSize = w * 0.24f
            canvas.drawText(timeStr, cx, cy, timePaint)

            // — Segundos: :SS —
            val secStr = String.format(":%02d", zonedDateTime.second)
            secondsPaint.textSize = w * 0.09f
            canvas.drawText(secStr, cx, cy + w * 0.12f, secondsPaint)

            // — FC del repositorio —
            val fc = SmartHealthRepository.fcFlow.value
            if (fc > 0) {
                fcPaint.textSize = w * 0.11f
                canvas.drawText("❤ $fc bpm", cx, cy + w * 0.28f, fcPaint)
            } else {
                fcNoDataPaint.textSize = w * 0.09f
                canvas.drawText("❤ -- bpm", cx, cy + w * 0.28f, fcNoDataPaint)
            }
        }

        override fun renderHighlightLayer(
            canvas: Canvas,
            bounds: Rect,
            zonedDateTime: ZonedDateTime,
            sharedAssets: Renderer.SharedAssets
        ) {
            // Capa de highlight requerida por la API (para el editor de caras)
            canvas.drawColor(
                renderParameters.highlightLayer?.backgroundTint ?: Color.TRANSPARENT
            )
        }
    }
}

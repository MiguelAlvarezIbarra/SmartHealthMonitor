package mx.utng.smarthealthmonitor.maai.data.models

data class LecturaFC(
    val id: Int,
    val valorBpm: Int,
    val hora: String,
    val esNormal: Boolean = valorBpm in 60..100
)

object MockData {
    val historialFC = listOf(
        LecturaFC(1, 78, "11:00"),
        LecturaFC(2, 82, "10:30"),
        LecturaFC(3, 76, "10:00"),
        LecturaFC(4, 95, "09:30", false),
        LecturaFC(5, 71, "09:00"),
        LecturaFC(6, 80, "08:30"),
        LecturaFC(7, 74, "08:00")
    )
    var fcActual = 78
    var pasosActual = 4250
}
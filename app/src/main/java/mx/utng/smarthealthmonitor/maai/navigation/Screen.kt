package mx.utng.smarthealthmonitor.maai.navigation

sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Dashboard : Screen("dashboard")
    object Historial : Screen("historial")
    object Alerta    : Screen("alerta")
}
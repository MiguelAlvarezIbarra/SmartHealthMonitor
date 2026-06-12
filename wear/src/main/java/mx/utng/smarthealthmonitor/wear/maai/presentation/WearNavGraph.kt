package mx.utng.smarthealthmonitor.wear.maai.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController

object WearScreens {
    const val DASHBOARD = "wear_dashboard"
    const val ALERTA = "wear_alerta"
}

@Composable
fun SmartHealthWearNavGraph() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = WearScreens.DASHBOARD
    ) {
        composable(WearScreens.DASHBOARD) {
            WearDashboardScreen(
                onAlertClick = {
                    navController.navigate(WearScreens.ALERTA)
                }
            )
        }
        composable(WearScreens.ALERTA) {
            val vm: WearDashboardViewModel = viewModel()
            val fc by vm.fc.collectAsState()
            WearAlertaScreen(
                fc = fc,
                onConfirmar = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }
    }
}

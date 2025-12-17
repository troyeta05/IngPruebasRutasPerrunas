import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tzilacatzin.rutasperrunas.screen.homedueño.HomeDueñoScreen
import com.tzilacatzin.rutasperrunas.screen.MapaPaseoScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    NavHost(navController = navHostController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                auth = auth,
                db = db,
                navController = navHostController,
                NavigateToSingup = { navHostController.navigate("singup") },
                NavigateToHomeDueño = { navHostController.navigate("homeDueño") },
                NavigateToHomePaseador = { navHostController.navigate("homePaseador") }
            )
        }
        composable("singup") {
            SingupScreen(
                auth = auth,
                db = db,
                NavigateToLogin = {
                    navHostController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("homeDueño") {
            HomeDueñoScreen(
                auth = auth,
                NavigationToLogin = { navHostController.navigate("login") },
                NavigationToAgregarMascota = { navHostController.navigate("agregarMascota") }
            )
        }
        composable("agregarMascota") {
            AgregarMascotaScreen(
                onNavigateBack = {
                    navHostController.popBackStack()
                }
            )
        }
        composable("homePaseador") {
            HomePaseadorScreen(
                auth = auth,
                NavigationToLogin = {
                    navHostController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavegarAlMapa = { paseoId ->
                    navHostController.navigate("mapaPaseo/$paseoId")
                }
            )
        }

        composable(
            route = "mapaPaseo/{paseoId}",
            arguments = listOf(navArgument("paseoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val paseoId = backStackEntry.arguments?.getString("paseoId") ?: ""
            MapaPaseoScreen(
                paseoId = paseoId,
                onPaseoFinalizado = {
                    navHostController.popBackStack()
                }
            )
        }
    }

}
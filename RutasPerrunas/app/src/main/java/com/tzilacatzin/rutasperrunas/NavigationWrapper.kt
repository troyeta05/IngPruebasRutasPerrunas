import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tzilacatzin.rutasperrunas.screen.homedueño.HomeDueñoScreen
import com.tzilacatzin.rutasperrunas.screen.homepaseador.HomePaseadorScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    NavHost(navController = navHostController, startDestination = "login") {
        composable("login") {
            // Forma correcta: Se elimina 'db' porque LoginScreen no lo necesita directamente
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
            // Forma correcta: Se elimina 'db' porque SingupScreen tampoco lo necesita directamente
            SingupScreen(
                auth = auth,
                db=db,
                NavigateToLogin = {
                    navHostController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("homeDueño") {
            HomeDueñoScreen()
        }
        composable("homePaseador") {
            HomePaseadorScreen()
        }
    }
}
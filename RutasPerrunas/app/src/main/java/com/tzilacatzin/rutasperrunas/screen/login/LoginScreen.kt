import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tzilacatzin.rutasperrunas.R
import com.tzilacatzin.rutasperrunas.model.buscarUsuarioEnFirestore
import com.tzilacatzin.rutasperrunas.ui.theme.AzulOscuro
import com.tzilacatzin.rutasperrunas.ui.theme.Blanco
import com.tzilacatzin.rutasperrunas.ui.theme.Negro
import com.tzilacatzin.rutasperrunas.ui.theme.VerdeOscuro
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavHostController,
    NavigateToSingup: () -> Unit = {},
    NavigateToHomeDueño: () -> Unit = {},
    NavigateToHomePaseador: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showEmptyFieldsDialog by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge,
                color = VerdeOscuro
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeOscuro,
                    unfocusedBorderColor = AzulOscuro,
                    focusedLabelColor = VerdeOscuro,
                    cursorColor = VerdeOscuro,
                    focusedTextColor = Negro,
                    unfocusedTextColor = Negro
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeOscuro,
                    unfocusedBorderColor = AzulOscuro,
                    focusedLabelColor = VerdeOscuro,
                    cursorColor = VerdeOscuro,
                    focusedTextColor = Negro,
                    unfocusedTextColor = Negro
                )
            )

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        showEmptyFieldsDialog = true
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user?.uid
                                if (uid != null) {
                                    scope.launch {
                                        val usuario = buscarUsuarioEnFirestore(uid = uid, db = db)
                                        if (usuario != null) {
                                            when (usuario.rol) {
                                                "Dueño" -> {
                                                    NavigateToHomeDueño()
                                                }

                                                "Paseador" -> {
                                                    NavigateToHomePaseador()
                                                }

                                                else -> {
                                                    showErrorDialog =
                                                        "Rol de usuario no reconocido."
                                                }
                                            }
                                        } else {
                                            showErrorDialog =
                                                "No se encontró un perfil para este usuario."
                                        }
                                    }
                                } else {
                                    showErrorDialog =
                                        "Ocurrió un error inesperado al iniciar sesión."
                                }
                            }
                            .addOnFailureListener {
                                showErrorDialog =
                                    "El correo electrónico o la contraseña son incorrectos."
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeOscuro,
                    contentColor = Blanco
                )
            ) {
                Text(text = "Entrar")
            }

            TextButton(onClick = { NavigateToSingup() }) {
                Text(
                    text = "¿No tienes una cuenta? Regístrate",
                    color = AzulOscuro
                )
            }
        }
    }

    showErrorDialog?.let { message ->
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Error de inicio de sesión") },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) {
                    Text("Aceptar", color = Blanco)
                }
            }
        )
    }

    if (showEmptyFieldsDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyFieldsDialog = false },
            title = { Text("Campos incompletos") },
            text = { Text("Por favor, introduce tu correo electrónico y contraseña.") },
            confirmButton = {
                Button(
                    onClick = { showEmptyFieldsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) {
                    Text("Aceptar", color = Blanco)
                }
            }
        )
    }
}
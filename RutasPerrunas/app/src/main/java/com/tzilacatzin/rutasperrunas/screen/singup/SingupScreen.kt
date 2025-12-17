import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tzilacatzin.rutasperrunas.R
import com.tzilacatzin.rutasperrunas.model.guardarDatosUsuarioEnFirestore
import com.tzilacatzin.rutasperrunas.ui.theme.AzulOscuro
import com.tzilacatzin.rutasperrunas.ui.theme.Blanco
import com.tzilacatzin.rutasperrunas.ui.theme.VerdeOscuro
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingupScreen(auth: FirebaseAuth, db: FirebaseFirestore, NavigateToLogin: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val radioOptions = listOf("Dueño", "Paseador")
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }

    // <<< 1. Añadir un CoroutineScope
    val scope = rememberCoroutineScope()

    var showEmptyFieldsDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

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
                text = "Crear Cuenta",
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
                    cursorColor = VerdeOscuro
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
                    cursorColor = VerdeOscuro
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { selectedOption = text },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VerdeOscuro,
                                unselectedColor = AzulOscuro
                            )
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        showEmptyFieldsDialog = true
                    } else {
                        showConfirmationDialog = true
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
                Text(text = "Registrarse")
            }

            TextButton(onClick = { NavigateToLogin() }) {
                Text(
                    text = "¿Ya tienes una cuenta? Inicia Sesión",
                    color = AzulOscuro
                )
            }
        }
    }


    if (showEmptyFieldsDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyFieldsDialog = false },
            title = { Text("Campos Incompletos") },
            text = { Text("Por favor, introduce tu correo y contraseña.") },
            confirmButton = {
                Button(
                    onClick = { showEmptyFieldsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) { Text("Aceptar", color = Blanco) }
            }
        )
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Registro") },
            text = { Text("¿Deseas crear una cuenta como '$selectedOption'?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        // <<< 2. Lanzar una coroutine para manejar las operaciones asíncronas
                        scope.launch {
                            try {
                                // Primero, intenta crear el usuario en Authentication
                                val authResult =
                                    auth.createUserWithEmailAndPassword(email, password).await()
                                val uid = authResult.user?.uid

                                if (uid != null) {
                                    // Si tiene éxito, intenta guardar los datos en Firestore
                                    val guardadoExitoso = guardarDatosUsuarioEnFirestore(
                                        uid,
                                        email,
                                        selectedOption,
                                        db
                                    )
                                    if (guardadoExitoso) {
                                        // Si todo sale bien, muestra el diálogo de éxito
                                        showSuccessDialog = true
                                    } else {
                                        // Si falla el guardado en Firestore, muestra error
                                        showErrorDialog =
                                            "No se pudo guardar la información del usuario."
                                    }
                                } else {
                                    showErrorDialog =
                                        "Ocurrió un error inesperado al obtener el identificador."
                                }
                            } catch (exception: Exception) {
                                // Si CUALQUIER .await() falla (autenticación o guardado), se captura aquí
                                showErrorDialog =
                                    exception.message ?: "Ocurrió un error durante el registro."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) { Text("Confirmar", color = Blanco) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text(
                        "Cancelar",
                        color = VerdeOscuro
                    )
                }
            }
        )
    }

    showErrorDialog?.let { message ->
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Error en el Registro") },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) { Text("Aceptar", color = Blanco) }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                NavigateToLogin()
            },
            title = { Text("¡Usuario Creado!") },
            text = { Text("Tu cuenta ha sido creada con éxito. Ahora puedes iniciar sesión.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        NavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
                ) { Text("Ir a Iniciar Sesión", color = Blanco) }
            }
        )
    }
}
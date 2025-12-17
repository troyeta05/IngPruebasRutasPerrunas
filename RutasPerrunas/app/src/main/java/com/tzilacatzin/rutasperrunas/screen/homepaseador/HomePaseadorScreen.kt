import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tzilacatzin.rutasperrunas.ui.theme.AzulClaro
import com.tzilacatzin.rutasperrunas.ui.theme.Blanco
import com.tzilacatzin.rutasperrunas.ui.theme.Negro
import com.tzilacatzin.rutasperrunas.ui.theme.VerdeOscuro
import com.tzilacatzin.rutasperrunas.viewmodel.PaseadorViewModel
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePaseadorScreen(
    auth: FirebaseAuth,
    NavigationToLogin: () -> Unit,
    viewModel: PaseadorViewModel = viewModel(),
    onNavegarAlMapa: (String) -> Unit
) {
    val paseos by viewModel.paseosDisponibles.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val db = Firebase.firestore
    val userId = auth.currentUser?.uid

    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var metodoDePagoLocal by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val doc = db.collection("users").document(userId).get().await()
                metodoDePagoLocal = doc.getString("metodoDePago")
            } catch (e: Exception) {
            }
        }
    }

    if (mostrarDialogoPago) {
        DialogoMetodoPagoPaseador(
            userId = userId,
            onDismiss = {
                mostrarDialogoPago = false
                if (userId != null) {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { doc ->
                            metodoDePagoLocal = doc.getString("metodoDePago")
                        }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paseos Disponibles", color = Blanco) },
                navigationIcon = {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menú",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeOscuro
                )
            )
        },
        containerColor = Blanco
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blanco)
                ) {
                    DropdownMenuItem(
                        text = { Text("Métodos de Pago", color = Negro) },
                        onClick = {
                            menuExpandido = false
                            mostrarDialogoPago = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = Negro) },
                        onClick = {
                            menuExpandido = false
                            auth.signOut()
                            NavigationToLogin()
                        }
                    )
                }
            }

            if (paseos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay solicitudes de paseo por ahora 😴", color = Negro)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(paseos) { paseo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (metodoDePagoLocal.isNullOrBlank()) {
                                        Toast.makeText(
                                            context,
                                            "Debes agregar un método de pago para tomar paseos.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        mostrarDialogoPago = true
                                    } else {
                                        onNavegarAlMapa(paseo.id)
                                    }
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Blanco)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Mascotas: ${paseo.nombresMascotas.joinToString(", ")}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Negro
                                )
                                Text(
                                    text = "Ganancia: $${paseo.costoTotal}",
                                    color = VerdeOscuro,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Estado: ${paseo.estado}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoMetodoPagoPaseador(
    userId: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var numeroTarjeta by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val db = Firebase.firestore

    // Cargar tarjeta existente
    LaunchedEffect(Unit) {
        if (userId != null) {
            try {
                val doc = db.collection("users").document(userId).get().await()
                val tarjetaGuardada = doc.getString("metodoDePago")
                if (tarjetaGuardada != null) {
                    numeroTarjeta = tarjetaGuardada
                }
            } catch (e: Exception) {
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Blanco,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Método de Pago",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Negro,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = numeroTarjeta,
                    onValueChange = {
                        if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                            numeroTarjeta = it
                        }
                    },
                    label = { Text("Número de Tarjeta (16 dígitos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulClaro,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isSaving) {
                    CircularProgressIndicator(color = AzulClaro)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (userId != null) {
                                    isSaving = true
                                    db.collection("users").document(userId)
                                        .set(
                                            mapOf("metodoDePago" to numeroTarjeta),
                                            SetOptions.merge()
                                        )
                                        .addOnSuccessListener {
                                            isSaving = false
                                            Toast.makeText(
                                                context,
                                                "Tarjeta guardada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onDismiss()
                                        }
                                        .addOnFailureListener {
                                            isSaving = false
                                            Toast.makeText(
                                                context,
                                                "Error al guardar",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            },
                            enabled = numeroTarjeta.length == 16,
                            colors = ButtonDefaults.buttonColors(containerColor = AzulClaro)
                        ) {
                            Text("Guardar", color = Negro)
                        }
                    }
                }
            }
        }
    }
}
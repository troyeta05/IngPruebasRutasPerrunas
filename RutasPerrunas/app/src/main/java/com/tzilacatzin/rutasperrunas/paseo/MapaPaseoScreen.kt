package com.tzilacatzin.rutasperrunas.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tzilacatzin.rutasperrunas.viewmodel.PaseadorViewModel

@Composable
fun MapaPaseoScreen(
    paseoId: String,
    viewModel: PaseadorViewModel = viewModel(),
    onPaseoFinalizado: () -> Unit
) {
    LaunchedEffect(paseoId) {
        viewModel.cargarPaseoEnTiempoReal(paseoId)
    }

    val paseoActual by viewModel.paseoActual.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState()

    var zonaPaseo by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(paseoActual) {
        paseoActual?.let { paseo ->
            val ubicacionPerro = LatLng(paseo.latitud, paseo.longitud)

            cameraPositionState.position = CameraPosition.fromLatLngZoom(ubicacionPerro, 17f)

            if (paseo.estado == "ACEPTADO" || paseo.estado == "EN_PASEO") {
                zonaPaseo = ubicacionPerro
            }
        }
    }

    if (paseoActual == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val paseo = paseoActual!!

        Box(modifier = Modifier.fillMaxSize()) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                Marker(
                    state = MarkerState(position = LatLng(paseo.latitud, paseo.longitud)),
                    title = "Ubicación del Perro"
                )

                zonaPaseo?.let { centro ->
                    Circle(
                        center = centro,
                        radius = 200.0, // 200 metros a la redonda
                        strokeColor = Color.Blue,
                        fillColor = Color(0x220000FF)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Mascotas: ${paseo.nombresMascotas.joinToString()}", style = MaterialTheme.typography.titleMedium)
                    Text("Estado actual: ${paseo.estado}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    when (paseo.estado) {
                        "SOLICITADO" -> {
                            Button(
                                onClick = { viewModel.aceptarPaseo(paseo.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Verde
                            ) {
                                Text("Aceptar Paseo")
                            }
                        }

                        "ACEPTADO" -> {
                            Text("Dirígete a la zona marcada para recoger al perro.")
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.iniciarPaseo(paseo.id) },
                                enabled = zonaPaseo != null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ya tengo al perro (Iniciar)")
                            }
                        }

                        "EN_PASEO" -> {
                            var codigo by remember { mutableStateOf("") }

                            OutlinedTextField(
                                value = codigo,
                                onValueChange = { codigo = it },
                                label = { Text("Código de fin (Pedir al dueño)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    viewModel.finalizarPaseo(paseo.id, codigo, paseo.codigoFin,
                                        onSuccess = { onPaseoFinalizado() },
                                        onError = {}
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                            ) {
                                Text("Finalizar Paseo")
                            }
                        }

                        "FINALIZADO" -> {
                            Text("¡Paseo completado con éxito! ✅", color = Color(0xFF4CAF50))
                            Button(onClick = { onPaseoFinalizado() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Salir")
                            }
                        }
                    }
                }
            }
        }
    }
}
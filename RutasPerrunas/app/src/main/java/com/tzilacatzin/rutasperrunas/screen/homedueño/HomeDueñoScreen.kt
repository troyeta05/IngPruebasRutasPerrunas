package com.tzilacatzin.rutasperrunas.screen.homedueño

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tzilacatzin.rutasperrunas.ui.theme.VerdeMenta

@Composable
fun HomeDueñoScreen() {
    // Usamos un Box para que ocupe toda la pantalla y poder centrar contenido fácilmente.
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el espacio disponible
            .background(VerdeMenta), // Establece el color de fondo
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        // Puedes agregar cualquier contenido aquí. Por ahora, un texto de ejemplo.
        Text(text = "Home del Dueño")
    }
}

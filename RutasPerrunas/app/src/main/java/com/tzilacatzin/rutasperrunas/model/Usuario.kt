package com.tzilacatzin.rutasperrunas.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await/**
 * Representa los datos adicionales de un usuario que se guardarán en Firestore.
 * No incluye la contraseña por motivos de seguridad.
 */
data class Usuario(
    val email: String = "",
    val rol: String = ""
)

suspend fun guardarDatosUsuarioEnFirestore(
    uid: String,
    email: String,
    rol: String,
    db: FirebaseFirestore
): Boolean {
    return try {
        val datosUsuario = Usuario(
            email = email,
            rol = rol
        )
        // Usa .set() y espera a que la tarea termine
        db.collection("usuarios").document(uid)
            .set(datosUsuario)
            .await()
        // Si await() no lanza una excepción, la operación fue exitosa.
        true
    } catch (e: Exception) {
        // Si ocurre cualquier error durante la escritura, se captura y se devuelve false.
        false
    }
}

suspend fun buscarUsuarioEnFirestore(
    uid: String,
    db: FirebaseFirestore
): Usuario? {
    return try {
        // Apunta al documento específico y espera a que la tarea termine
        val documentSnapshot = db.collection("usuarios").document(uid)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            // Si el documento existe, conviértelo a objeto Usuario y devuélvelo
            documentSnapshot.toObject(Usuario::class.java)
        } else {
            // Si el documento no existe, devuelve null
            null
        }
    } catch (e: Exception) {
        // Si ocurre cualquier error durante la lectura, devuelve null
        null
    }
}

package com.tzilacatzin.rutasperrunas.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
        db.collection("usuarios").document(uid)
            .set(datosUsuario)
            .await()
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun buscarUsuarioEnFirestore(
    uid: String,
    db: FirebaseFirestore
): Usuario? {
    return try {
        val documentSnapshot = db.collection("usuarios").document(uid)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            documentSnapshot.toObject(Usuario::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

package com.tzilacatzin.rutasperrunas

import NavigationWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tzilacatzin.rutasperrunas.ui.theme.RutasPerrunasTheme

class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()

            RutasPerrunasTheme {
                NavigationWrapper(navHostController,auth,db)
            }
        }
    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //Navegar a home
        }
    }
}
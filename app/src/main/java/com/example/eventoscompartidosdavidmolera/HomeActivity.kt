package com.example.eventoscompartidosdavidmolera

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventoscompartidosdavidmolera.databinding.ActivityHomeBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import modelos.User

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        val prov:String = bundle?.getString("provider").toString()
        val us = intent.getSerializableExtra("user") as User

        //Guardado de datos para toda la aplicación en la sesión.
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs?.putString("email",bundle?.getString("email").toString())
        prefs?.putString("provider",bundle?.getString("provider").toString())
        prefs?.apply () //Con estos datos guardados en el fichero de sesión, aunque la app se detenga tendremos acceso a los mismos.

        binding.btCerrar.setOnClickListener{
            val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs?.clear() //Al cerrar sesión borramos los datos
            prefs?.apply ()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}
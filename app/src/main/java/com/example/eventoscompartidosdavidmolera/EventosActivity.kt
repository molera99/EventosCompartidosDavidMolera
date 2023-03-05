package com.example.eventoscompartidosdavidmolera

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eventoscompartidosdavidmolera.databinding.ActivityEventosBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityHomeBinding
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import modelos.Events
import java.util.*

class EventosActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventosBinding
    val db = Firebase.firestore
    lateinit var evento:Events
    lateinit var latlong:LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmento=MapsFragment()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.miFragmento, fragmento)
        fragmentTransaction.commit()

        binding.btCrearEvent.setOnClickListener{
            Toast.makeText(this, "Estás en ${fragmento.latlong.latitude}", Toast.LENGTH_SHORT).show()


            var even = hashMapOf(
                "nombre" to binding.etNombreEvento.text.toString(),
                "latitud" to fragmento.latlong.latitude,
                "longitud" to fragmento.latlong.longitude,
            )

            // Añadimos el nuevo documento.
            db.collection("events") //Si hubiera un campo duplicado, lo remplaza.
                .add(even)
                .addOnSuccessListener {
                    Log.e(TAG, "Documento añadido.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e.cause)
                }

        }
    }

}
package com.example.eventoscompartidosdavidmolera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventoscompartidosdavidmolera.databinding.ActivityEventosBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityHomeBinding
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EventosActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventosBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmento=MapsFragment()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.miFragmento, fragmento)
        fragmentTransaction.commit()
    }

}
package com.example.eventoscompartidosdavidmolera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventoscompartidosdavidmolera.databinding.ActivityAltaUsuarioEventoBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityBajaUsuarioEventoBinding

class BajaUsuarioEventoActivity : AppCompatActivity() {
    lateinit var binding: ActivityBajaUsuarioEventoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBajaUsuarioEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
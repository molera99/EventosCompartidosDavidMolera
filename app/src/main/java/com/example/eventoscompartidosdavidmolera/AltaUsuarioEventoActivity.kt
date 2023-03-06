package com.example.eventoscompartidosdavidmolera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventoscompartidosdavidmolera.databinding.ActivityAltaUsuarioEventoBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityEliminarEventoBinding

class AltaUsuarioEventoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAltaUsuarioEventoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAltaUsuarioEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
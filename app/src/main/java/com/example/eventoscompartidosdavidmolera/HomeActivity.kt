package com.example.eventoscompartidosdavidmolera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventoscompartidosdavidmolera.databinding.ActivityHomeBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
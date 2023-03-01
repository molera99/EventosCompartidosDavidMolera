package com.example.eventoscompartidosdavidmolera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val RC_SIGN_IN = 1
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegistrar.setOnClickListener {
            if (binding.edEmail.text.isNotEmpty() && binding.edPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.edEmail.text.toString(),binding.edPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        irHome(it.result?.user?.email?:"",ProviderType.BASIC)  //Esto de los interrogantes es por si está vacío el email, que enviaría una cadena vacía.
                    } else {
                        showAlert()
                    }
                }
            }
        }

        binding.btLogin.setOnClickListener {
            if (binding.edEmail.text.isNotEmpty() && binding.edPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.edEmail.text.toString(),binding.edPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        irHome(it.result?.user?.email?:"",ProviderType.BASIC)  //Esto de los interrogantes es por si está vacío el email.
                    } else {
                        showAlert()
                    }
                }
            }
        }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun irHome(email:String, provider:ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }
}
package com.example.eventoscompartidosdavidmolera

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import funcionalidades.ProviderType

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    val RC_SIGN_IN = 1
    lateinit var auth: FirebaseAuth
    var db = Firebase.firestore//Variable con la que accederemos a Firestore. Será una instancia a la bd.
    private val TAG = "David"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegistro.setOnClickListener{
            if (binding.etEmail.text.isNotEmpty() && binding.etNombre.text.isNotEmpty() && binding.etPassword.text.isNotEmpty() && binding.etEdad.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        var user = hashMapOf(
                            "email" to binding.etEmail.text.toString(),
                            "password" to binding.etPassword.text.toString(),
                            "name" to binding.etNombre.text.toString(),
                            "age" to binding.etEdad.text.toString().toInt(),
                            "rol" to "user",
                            "activate" to false
                        )

                        // Add a new document with a generated ID
                        db.collection("users")
                            .add(user)
                            .addOnSuccessListener {
                                Log.e(TAG, "Documento añadido con ID: ${it.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e.cause)
                            }
                        AlertDialog.Builder(this)
                            .setTitle("REGISTRO")
                            .setMessage("Registro completado con exito")
                            .setPositiveButton(android.R.string.ok,
                                DialogInterface.OnClickListener { dialog, which ->
                                    var intentMain = Intent(this,MainActivity::class.java)
                                    startActivity(intentMain)
                                })
                            .show()
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

}
package com.example.eventoscompartidosdavidmolera

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import funcionalidades.ProviderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import modelos.User

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val RC_SIGN_IN = 1
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var us:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegistrar.setOnClickListener {
            var intentRegistro = Intent(this,RegisterActivity::class.java)
            startActivity(intentRegistro)
        }

        binding.btLogin.setOnClickListener {
            if (binding.edEmail.text.isNotEmpty() && binding.edPass.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.edEmail.text.toString(),binding.edPass.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        runBlocking {
                            val job : Job = launch(context = Dispatchers.Default) {
                                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                                obtenerDatos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
                            }
                            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
                            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
                        }

                        if(us.activate){
                            irHome(it.result?.user?.email?:"", ProviderType.BASIC)  //Esto de los interrogantes es por si está vacío el email.
                        }else{
                            AlertDialog.Builder(this)
                                .setTitle("ERROR")
                                .setMessage("Tu cuenta debe activarla un Admin")
                                .setPositiveButton(android.R.string.ok,
                                    DialogInterface.OnClickListener { dialog, which ->
                                    })
                                .show()
                        }

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

    private fun irHome(email:String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }
    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("users")
                .whereEqualTo("email",binding.edEmail.text.toString())
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }
    private fun obtenerDatos(datos: QuerySnapshot?) {
        for(dc: DocumentChange in datos?.documentChanges!!){
            if (dc.type == DocumentChange.Type.ADDED){

                us = User(
                    dc.document.get("email").toString(),
                    dc.document.get("password").toString(),
                    dc.document.get("name").toString(),
                    dc.document.get("age").toString().toInt(),
                    dc.document.get("rol").toString(),
                    dc.document.get("activate").toString().toBoolean()
                )
            }
        }
    }
}
package com.example.eventoscompartidosdavidmolera

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.example.eventoscompartidosdavidmolera.databinding.ActivityHomeAdminBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import modelos.Listas
import modelos.User
import java.util.*
import kotlin.collections.ArrayList

class HomeAdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeAdminBinding
    val db = Firebase.firestore
    var users=ArrayList<String>()
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                obtenerDatos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        for(i in Listas.listaUsers.indices){
            users.add(Listas.listaUsers[i].email)
        }
        print(Listas.listaUsers.count())

        val adaptadorUsers = ArrayAdapter(this, R.layout.item_spinner,R.id.txtItemSpinner,users)
        binding.spUsuarios.adapter = adaptadorUsers

        binding.spUsuarios.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                user= Listas.listaUsers[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
        binding.btGuardar.setOnClickListener{
            var activate=true
            if (binding.rbActivar.isChecked){
                activate=true
            }else if (binding.rbDesactivar.isChecked){
                activate=false
            }

            var us = hashMapOf(
                "email" to user.email,
                "password" to user.password,
                "name" to user.name,
                "age" to user.age,
                "rol" to user.rol,
                "activate" to activate
            )

            // Add a new document with a generated ID
            db.collection("users")
                .document(user.id)  //Si hubiera un campo duplicado, lo reemplaza.
                .set(us)
                .addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setTitle("Modificacion")
                        .setMessage("Cuenta modificada con exito")
                        .setPositiveButton(android.R.string.ok,
                            DialogInterface.OnClickListener { dialog, which ->
                            })
                        .show()
                    Log.e(TAG, "Documento modificado.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e.cause)
                }
        }

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        val prov:String = bundle?.getString("provider").toString()
        val us = intent.getSerializableExtra("user") as User

        //Guardado de datos para toda la aplicación en la sesión.
        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs?.putString("email",bundle?.getString("email").toString())
        prefs?.putString("provider",bundle?.getString("provider").toString())
        prefs?.apply () //Con estos datos guardados en el fichero de sesión, aunque la app se detenga tendremos acceso a los mismos.

        binding.btCerrarAdmin.setOnClickListener{
            val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs?.clear() //Al cerrar sesión borramos los datos
            prefs?.apply ()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }
    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("users")
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

                var us = User(
                    dc.document.id,
                    dc.document.get("email").toString(),
                    dc.document.get("password").toString(),
                    dc.document.get("name").toString(),
                    dc.document.get("age").toString().toInt(),
                    dc.document.get("rol").toString(),
                    dc.document.get("activate").toString().toBoolean()
                )
                Listas.listaUsers.add(us)
            }
        }
    }
}
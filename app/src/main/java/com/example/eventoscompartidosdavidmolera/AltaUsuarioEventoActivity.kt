package com.example.eventoscompartidosdavidmolera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.eventoscompartidosdavidmolera.databinding.ActivityAltaUsuarioEventoBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityEliminarEventoBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import modelos.Events
import modelos.Listas
import modelos.User
import modelos.UserEvent

class AltaUsuarioEventoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAltaUsuarioEventoBinding
    val db = Firebase.firestore
    var even=ArrayList<String>()
    lateinit var usId:String
    lateinit var eventId:String
    var usersEvent=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAltaUsuarioEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                obtenerDatos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            val job2 : Job = launch(context = Dispatchers.Default) {
                val datos2 : QuerySnapshot = getDataFromFireStore2() as QuerySnapshot //Obtenermos la colección
                obtenerDatos2(datos2 as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }

            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
            job2.join()
        }
        for(i in Listas.listaEvents.indices){
            even.add(Listas.listaEvents[i].nombre)
        }
        for(i in Listas.listaUsers.indices){
            usersEvent.add(Listas.listaUsers[i].name)
        }


        val adaptadorEvents = ArrayAdapter(this, R.layout.item_spinner,R.id.txtItemSpinner,even)
        val adaptadorUsers = ArrayAdapter(this, R.layout.item_spinner,R.id.txtItemSpinner,usersEvent)
        binding.spEventos3.adapter = adaptadorEvents
        binding.spUsuarios3.adapter=adaptadorUsers
        binding.spEventos3.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                eventId= Listas.listaEvents[pos].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
        binding.spUsuarios3.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                usId= Listas.listaUsers[pos].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
        binding.btAltaUsuario.setOnClickListener{
            var user= hashMapOf(
                "fotos" to arrayListOf<String>(),
                "ubicaciones" to arrayListOf<String>(),
                "horaLlegada" to ""
            )
            db.collection("events")
                .document(eventId)
                .collection("users")
                .document(usId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario añadido al evento", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "fallo al añadir documento",Toast.LENGTH_SHORT).show()
                }
            var intentAdmin= Intent(this,HomeAdminActivity::class.java)
            startActivity(intentAdmin)
        }
    }

    suspend fun getDataFromFireStore()  : QuerySnapshot? {
        return try{
            val data = db.collection("events")
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
                var userEvents= arrayListOf<UserEvent>()
                var ev = Events(
                    dc.document.id,
                    dc.document.get("nombre").toString(),
                    dc.document.get("latitud").toString().toDouble(),
                    dc.document.get("longitud").toString().toDouble(),
                    userEvents
                )
                Listas.listaEvents.add(ev)
            }
        }
    }

    suspend fun getDataFromFireStore2()  : QuerySnapshot? {
        return try{
            val data = db.collection("users")
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }
    private fun obtenerDatos2(datos: QuerySnapshot?) {
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
                Listas.listaUsers.clear()
                Listas.listaUsers.add(us)
            }
        }
    }


}
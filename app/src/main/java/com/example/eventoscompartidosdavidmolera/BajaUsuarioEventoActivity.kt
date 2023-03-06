package com.example.eventoscompartidosdavidmolera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.eventoscompartidosdavidmolera.databinding.ActivityAltaUsuarioEventoBinding
import com.example.eventoscompartidosdavidmolera.databinding.ActivityBajaUsuarioEventoBinding
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
import modelos.UserEvent

class BajaUsuarioEventoActivity : AppCompatActivity() {
    lateinit var binding: ActivityBajaUsuarioEventoBinding
    val db = Firebase.firestore
    var even=ArrayList<String>()
    lateinit var eventId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBajaUsuarioEventoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            val job : Job = launch(context = Dispatchers.Default) {
                val datos : QuerySnapshot = getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                obtenerDatos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        for(i in Listas.listaEvents.indices){
            even.add(Listas.listaEvents[i].nombre)
        }

        val adaptadorEvents = ArrayAdapter(this, R.layout.item_spinner,R.id.txtItemSpinner,even)
        binding.spEventos2.adapter = adaptadorEvents

        binding.spEventos2.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                eventId= Listas.listaEvents[pos].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
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
}
package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter

class ResultadosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)

        val respuestasCorrectas = intent.getIntExtra("respuestasCorrectas", 0)
        val totalPreguntas = intent.getIntExtra("totalPreguntas", 0)
        val respuestasElegidas = intent.getStringExtra("respuestasElegidas") // Recibir las respuestas elegidas

        val textViewResultados = findViewById<TextView>(R.id.textViewResultados)
        textViewResultados.text = "¡Quiz Finalizado! $respuestasCorrectas / $totalPreguntas"

        val buttonGuardarResultados = findViewById<Button>(R.id.buttonGuardarResultados)
        buttonGuardarResultados.setOnClickListener {
            val resultadosJson = JSONObject()
            resultadosJson.put("respuestasCorrectas", respuestasCorrectas)
            resultadosJson.put("totalPreguntas", totalPreguntas)
            resultadosJson.put("respuestasElegidas", JSONArray(respuestasElegidas)) // Agregar respuestas al JSON
            guardarResultadosEnServidor(resultadosJson)
        }

        val buttonJugarOtraVez = findViewById<Button>(R.id.buttonJugarOtraVez)
        buttonJugarOtraVez.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun guardarResultadosEnServidor(resultados: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlString = "http://dam.inspedralbes.cat:20000/guardarResultados"
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                urlConnection.setRequestProperty("Content-Type", "application/json")

                OutputStreamWriter(urlConnection.outputStream).use {
                    it.write(resultados.toString())
                    it.flush()
                }

                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

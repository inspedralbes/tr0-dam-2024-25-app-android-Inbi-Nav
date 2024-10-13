package com.example.quizapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var preguntas: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchPreguntas("http://dam.inspedralbes.cat:20000/preguntas")
    }

    private fun fetchPreguntas(urlString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = realizarSolicitud(urlString)
            if (result != null) {
                withContext(Dispatchers.Main) {
                    val jsonObject = JSONObject(result)
                    preguntas = jsonObject.getJSONArray("preguntas")

                    val intent = Intent(this@MainActivity, QuizActivity::class.java)
                    intent.putExtra("preguntas", preguntas.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private suspend fun realizarSolicitud(urlString: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class QuizActivity : AppCompatActivity() {

    private lateinit var preguntas: JSONArray
    private var preguntaActual = 0
    private var respuestasCorrectas = 0
    private var respuestasElegidas = mutableListOf<JSONObject>()
    private lateinit var timer: CountDownTimer
    private var tiempoRestante: Long = 30000
    private lateinit var textViewTiempo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTiempo = findViewById(R.id.textViewTiempo)

        val preguntasString = intent.getStringExtra("preguntas")
        preguntas = JSONArray(preguntasString)

        mostrarPregunta()
        iniciarCronometro()

        val buttonsOpciones = listOf(
            findViewById<Button>(R.id.buttonOpcion1),
            findViewById<Button>(R.id.buttonOpcion2),
            findViewById<Button>(R.id.buttonOpcion3),
            findViewById<Button>(R.id.buttonOpcion4)
        )

        buttonsOpciones.forEach { button ->
            button.setOnClickListener { verificarRespuesta(button.text.toString()) }
        }
    }

    private fun iniciarCronometro() {
        timer = object : CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                textViewTiempo.text = "Tiempo restante: ${millisUntilFinished / 1000} segundos"
            }

            override fun onFinish() {
                mostrarResultados()
            }
        }.start()
    }

    private fun mostrarPregunta() {
        if (preguntaActual < preguntas.length()) {
            val pregunta = preguntas.getJSONObject(preguntaActual)

            findViewById<TextView>(R.id.textViewPregunta).text = pregunta.getString("pregunta")

            val imageViewPregunta = findViewById<ImageView>(R.id.imageViewPregunta)
            val imageUrl = pregunta.optString("imagen", "")
            if (imageUrl.isNotEmpty()) {
                Picasso.get().load(imageUrl).into(imageViewPregunta)
            } else {
                imageViewPregunta.setImageResource(0)
            }

            val respuestas = desordenarRespuestas(pregunta)
            val buttonsOpciones = listOf(
                findViewById<Button>(R.id.buttonOpcion1),
                findViewById<Button>(R.id.buttonOpcion2),
                findViewById<Button>(R.id.buttonOpcion3),
                findViewById<Button>(R.id.buttonOpcion4)
            )

            buttonsOpciones.forEachIndexed { index, button ->
                button.text = respuestas[index]
            }
        } else {
            mostrarResultados()
        }
    }

    private fun verificarRespuesta(respuestaSeleccionada: String) {
        val pregunta = preguntas.getJSONObject(preguntaActual)
        val respuestaCorrecta = pregunta.getString("respuesta_correcta")

        val resultado = JSONObject()
        resultado.put("pregunta", pregunta.getString("pregunta"))
        resultado.put("respuestaElegida", respuestaSeleccionada)
        resultado.put("respuestaCorrecta", respuestaCorrecta)

        respuestasElegidas.add(resultado)

        if (respuestaSeleccionada == respuestaCorrecta) {
            respuestasCorrectas++
        }

        preguntaActual++
        if (preguntaActual < preguntas.length()) {
            mostrarPregunta()
        } else {
            mostrarResultados()
        }
    }

    private fun mostrarResultados() {
        timer.cancel()

        val intent = Intent(this, ResultadosActivity::class.java)
        intent.putExtra("respuestasCorrectas", respuestasCorrectas)
        intent.putExtra("totalPreguntas", preguntas.length())
        intent.putExtra("respuestasElegidas", JSONArray(respuestasElegidas).toString()) // Enviar respuestas elegidas
        startActivity(intent)
        finish()
    }

    private fun desordenarRespuestas(pregunta: JSONObject): List<String> {
        val respuestas = mutableListOf<String>()
        val respuestasIncorrectas = pregunta.getJSONArray("respuestas_incorrectas")

        for (i in 0 until respuestasIncorrectas.length()) {
            respuestas.add(respuestasIncorrectas.getString(i))
        }

        respuestas.add(pregunta.getString("respuesta_correcta"))
        respuestas.shuffle()
        return respuestas
    }
}

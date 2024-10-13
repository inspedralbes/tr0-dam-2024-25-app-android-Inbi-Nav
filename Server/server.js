const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const app = express();

app.use(cors());
app.use(express.json());

const preguntasFile = './preguntas.json';
const resultadosCarpeta = './resultados';

function crearCarpetaSiNoExiste(carpeta) {
  if (!fs.existsSync(carpeta)) {
    try {
      fs.mkdirSync(carpeta, { recursive: true });
      console.log(`Carpeta creada: ${carpeta}`);
    } catch (error) {
      console.error(`Error al crear la carpeta: ${error.message}`);
    }
  }
}

crearCarpetaSiNoExiste(resultadosCarpeta);

app.get('/preguntas', (req, res) => {
  fs.readFile(preguntasFile, (err, data) => {
    if (err) return res.status(500).send('Error al leer el archivo de preguntas');
    res.json(JSON.parse(data));
  });
});

app.post('/preguntas', (req, res) => {
  const nuevaPregunta = req.body;
  fs.readFile(preguntasFile, (err, data) => {
    if (err) return res.status(500).send('Error al leer el archivo de preguntas');
    const preguntas = JSON.parse(data);
    preguntas.preguntas.push(nuevaPregunta);
    fs.writeFile(preguntasFile, JSON.stringify(preguntas, null, 2), (err) => {
      if (err) return res.status(500).send('Error al escribir en el archivo de preguntas');
      res.status(201).send('Pregunta añadida');
    });
  });
});

app.put('/preguntas/:indice', (req, res) => {
  const indice = parseInt(req.params.indice);
  const preguntaActualizada = req.body;
  fs.readFile(preguntasFile, (err, data) => {
    if (err) return res.status(500).send('Error al leer el archivo de preguntas');
    const preguntas = JSON.parse(data);
    if (indice < 0 || indice >= preguntas.preguntas.length) {
      return res.status(404).send('Pregunta no encontrada');
    }
    preguntas.preguntas[indice] = preguntaActualizada;
    fs.writeFile(preguntasFile, JSON.stringify(preguntas, null, 2), (err) => {
      if (err) return res.status(500).send('Error al escribir en el archivo de preguntas');
      res.send('Pregunta actualizada');
    });
  });
});

app.delete('/preguntas/:indice', (req, res) => {
  const indice = parseInt(req.params.indice);
  fs.readFile(preguntasFile, (err, data) => {
    if (err) return res.status(500).send('Error al leer el archivo de preguntas');
    const preguntas = JSON.parse(data);
    if (indice < 0 || indice >= preguntas.preguntas.length) {
      return res.status(404).send('Pregunta no encontrada');
    }
    preguntas.preguntas.splice(indice, 1);
    fs.writeFile(preguntasFile, JSON.stringify(preguntas, null, 2), (err) => {
      if (err) return res.status(500).send('Error al escribir en el archivo de preguntas');
      res.send('Pregunta eliminada');
    });
  });
});

app.post('/guardarResultados', (req, res) => {
  const resultados = req.body;
  const nombreArchivo = `resultado_${Date.now()}.json`;
  const rutaArchivo = path.join(resultadosCarpeta, nombreArchivo);
  fs.writeFile(rutaArchivo, JSON.stringify(resultados, null, 2), (err) => {
    if (err) {
      return res.status(500).send(':( Error al guardar los resultados :(');
    }
    res.status(201).send(':) Resultados guardados correctamente :)');
  });
});

app.listen(20000, () => {
  console.log('Servidor ejecutándose en http://dam.inspedralbes.cat:20000');
});

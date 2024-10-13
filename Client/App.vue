<template>
  <div id="app">
    <div class="container">
      <div class="preguntas">
        <h2>QUIZ CRUD</h2>
        <div v-for="(pregunta, index) in preguntas" :key="index" class="PreguntaContainer">
          <img :src="pregunta.imagen" alt="Imagen de la película" class="ImagenPeliculaContainer" />
          <h3>{{ pregunta.pregunta }}</h3>
          <ul>
            <li v-for="(opcion, opcionIndex) in opciones(pregunta)" :key="opcionIndex">
              {{ opcion }} <span v-if="pregunta.respuesta_correcta === opcion">⭕</span>
            </li>
          </ul>
          <button @click="eliminarPregunta(index)" class="eliminar">Eliminar</button>
          <button @click="editarPregunta(index)" class="editar">Editar</button>
        </div>
      </div>

      <div class="formulario" v-if="mostrarFormulario">
        <h2>{{ editIndex === null ? 'Añadir Pregunta' : 'Editar Pregunta' }}</h2>
        <form @submit.prevent="editIndex === null ? guardarPregunta() : actualizarPregunta()">
          <input v-model="nuevaPregunta.pregunta" placeholder="Escribe la pregunta" required />
          <input v-model="nuevaPregunta.imagen" placeholder="URL de la imagen" required />
          <div v-for="(opcion, index) in nuevaPregunta.opciones" :key="index" class="opcion">
            <input v-model="nuevaPregunta.opciones[index]" placeholder="Opción de respuesta" required />
            <input type="radio" v-model="nuevaPregunta.correctaIndex" :value="index" />
            <label>Correcta</label>
          </div>
          <button v-if="editIndex === null" type="submit" class="guardar">Añadir Pregunta</button>
          <button v-else type="button" @click="actualizarPregunta()" class="guardar">Guardar Cambios</button>
        </form>
      </div>

      <div class="control-form">
        <button @click="mostrarFormulario = !mostrarFormulario" class="añadir">
          {{ mostrarFormulario ? 'Ocultar Formulario' : 'Añadir Pregunta' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const preguntas = ref([]);
    const nuevaPregunta = ref({
      pregunta: '',
      imagen: '',
      opciones: ['', '', '', ''],
      correctaIndex: 0,
    });
    const editIndex = ref(null);
    const mostrarFormulario = ref(false); 

    const obtenerPreguntas = async () => {
      try {
        const res = await fetch('http://dam.inspedralbes.cat:20000/preguntas');
        const data = await res.json();
        preguntas.value = data.preguntas.map((pregunta) => ({
          ...pregunta,
          opciones: [pregunta.respuesta_correcta, ...pregunta.respuestas_incorrectas].sort(() => Math.random() - 0.5),
          correctaIndex: 0,
        }));
      } catch (error) {
        console.error('Error al obtener preguntas:', error);
      }
    };

    const guardarPregunta = async () => {
      try {
        const preguntaData = {
          pregunta: nuevaPregunta.value.pregunta,
          respuesta_correcta: nuevaPregunta.value.opciones[nuevaPregunta.value.correctaIndex],
          respuestas_incorrectas: nuevaPregunta.value.opciones.filter((opcion, index) => index !== nuevaPregunta.value.correctaIndex),
          imagen: nuevaPregunta.value.imagen,
        };

        await fetch('http://dam.inspedralbes.cat:20000/preguntas', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(preguntaData),
        });

        nuevaPregunta.value = { pregunta: '', imagen: '', opciones: ['', '', '', ''], correctaIndex: 0 };
        obtenerPreguntas();
        mostrarFormulario.value = false; 
      } catch (error) {
        console.error('Error al guardar pregunta:', error);
      }
    };

    const actualizarPregunta = async () => {
      try {
        const preguntaData = {
          pregunta: nuevaPregunta.value.pregunta,
          respuesta_correcta: nuevaPregunta.value.opciones[nuevaPregunta.value.correctaIndex],
          respuestas_incorrectas: nuevaPregunta.value.opciones.filter((opcion, index) => index !== nuevaPregunta.value.correctaIndex),
          imagen: nuevaPregunta.value.imagen,
        };

        await fetch(`http://dam.inspedralbes.cat:20000/preguntas/${editIndex.value}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(preguntaData),
        });

        nuevaPregunta.value = { pregunta: '', imagen: '', opciones: ['', '', '', ''], correctaIndex: 0 };
        obtenerPreguntas();
        mostrarFormulario.value = false; 
        editIndex.value = null; 
      } catch (error) {
        console.error('Error al actualizar pregunta:', error);
      }
    };

    const eliminarPregunta = async (indice) => {
      try {
        await fetch(`http://dam.inspedralbes.cat:20000/preguntas/${indice}`, { method: 'DELETE' });
        obtenerPreguntas();
      } catch (error) {
        console.error('Error al eliminar pregunta:', error);
      }
    };

    const editarPregunta = (indice) => {
      editIndex.value = indice;
      const pregunta = preguntas.value[indice];
      nuevaPregunta.value = {
        pregunta: pregunta.pregunta,
        imagen: pregunta.imagen,
        opciones: pregunta.opciones,
        correctaIndex: pregunta.opciones.indexOf(pregunta.respuesta_correcta),
      };
      mostrarFormulario.value = true; 
    };

    const opciones = (pregunta) => pregunta.opciones;

    onMounted(obtenerPreguntas);

    return {
      preguntas,
      nuevaPregunta,
      editIndex,
      opciones,
      guardarPregunta,
      eliminarPregunta,
      editarPregunta,
      mostrarFormulario,
      actualizarPregunta,
    };
  },
};
</script>

<style>
.container {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.preguntas {
  flex: 4;
  padding: 20px;
}

.formulario {
  height: fit-content;
  padding: 20px; 
  margin-left: 250px; 
}

.PreguntaContainer {
  border: 1px solid #ddd;
  padding: 10px;
  margin-bottom: 10px;
  width: 300%; 
  background-color: #f9f9f9;
}

.ImagenPeliculaContainer {
  width: 100%;
  max-width: 300px;
  height: auto;
}

form {
  padding-left: 50px; 
}

form > * {
  margin-bottom: 20px;
  padding: 1px;
}

button {
  cursor: pointer;
  padding: 10px;
  border: none;
  border-radius: 5px;
  color: #fff;
  margin-right: 10px; /* Añadir margen a la derecha de los botones */
}

button.eliminar {
  background-color: #ff4c4c;
}

button.editar {
  background-color: #4caf50; 
}

button.añadir {
  background-color: #4caf50; 
  padding: 5px;
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 2px;
  margin-top: 15px;
  margin-left: 140px;
}

button.guardar {
  background-color: #4caf50; 
}
</style>

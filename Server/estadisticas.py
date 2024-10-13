import os
import json
import matplotlib.pyplot as plt

ruta_resultados = './resultados/'

TotalRondas = 0
PregTotal = 0
RespuestasCorrectasTotal = 0
RespuestasCorrectasRonda = [] 

for archivo in os.listdir(ruta_resultados):
    if archivo.endswith('.json'):
        with open(os.path.join(ruta_resultados, archivo), 'r') as f:
            resultado = json.load(f)

            TotalRondas += 1
            PregTotal += resultado['totalPreguntas']
            RespuestasCorrectasTotal += resultado['respuestasCorrectas']
            RespuestasCorrectasRonda.append(resultado['respuestasCorrectas'])

if TotalRondas > 0:
    PreguntasPromedio = PregTotal / TotalRondas
    RespuestasCorrectaPromedio = RespuestasCorrectasTotal / TotalRondas
    PorcentajeAciertos = (RespuestasCorrectasTotal / PregTotal) * 100

    print(f"Número total de rondas: {TotalRondas}")
    print(f"Promedio de preguntas por ronda: {PreguntasPromedio:.2f}")
    print(f"Promedio de respuestas correctas por ronda: {RespuestasCorrectaPromedio:.2f}")
    print(f"Porcentaje de respuestas correctas: {PorcentajeAciertos:.2f}%")

    Rondas = list(range(1, TotalRondas + 1)) 
    plt.bar(Rondas, RespuestasCorrectasRonda)
    plt.xlabel('Ronda')
    plt.ylabel('Respuestas Correctas')
    plt.title('Respuestas Correctas por Ronda')
    plt.show()

else:
    print("No se encontraron resultados :( ")

<div align="center">
	<!--<img src="https://raw.githubusercontent.com/miguerubsk/IC2-Reactor-Alg/master/.github/assets/logo.png" alt="IC2 Reactor Alg Logo" width="120" /> -->
	<h1>IC2 Reactor Alg</h1>
	<p>
		Algoritmo evolutivo para crear diseños de reactores nucleares en el mod IndustrialCraft 2 de Minecraft.
	</p>
	<p>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg"><img src="https://img.shields.io/github/license/miguerubsk/IC2-Reactor-Alg" alt="License"></a>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/issues"><img src="https://img.shields.io/github/issues/miguerubsk/IC2-Reactor-Alg" alt="Issues"></a>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/stargazers"><img src="https://img.shields.io/github/stars/miguerubsk/IC2-Reactor-Alg" alt="Stars"></a>
		<br>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/codeql.yml"><img src="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/codeql.yml/badge.svg"></a>
	</p>
</div>

---

## 🚀 Descripción

IC2-Reactor-Alg es un algoritmo evolutivo para generar automáticamente configuraciones óptimas de reactores nucleares en el mod IndustrialCraft 2 de Minecraft. Utiliza simulaciones basadas en una versión modificada de [Ic2ExpReactorPlanner](https://github.com/MauveCloud/Ic2ExpReactorPlanner) para evaluar la eficiencia y seguridad de cada diseño.

## 🧩 Características

- Algoritmo genético para optimización de diseños.
- Simulación detallada de componentes y comportamiento térmico.
- Configuración flexible mediante archivo `config.txt`.
- Resultados exportados en `result.txt`.
- Modularidad y fácil extensión de componentes.

## 📦 Estructura del proyecto

```text
IC2-Reactor-Alg/
├── src/
│   ├── geneticAlg/         # Lógica del algoritmo genético
│   └── Simulator/          # Simulador de reactores y componentes
├── config.txt              # Configuración de parámetros evolutivos
├── result.txt              # Resultados de la simulación
├── README.md               # Este archivo
├── LICENSE.md              # Licencia GPLv3
└── ...
```

## ⚙️ Instalación y uso

1. Clona el repositorio:
   ```sh
   git clone https://github.com/miguerubsk/IC2-Reactor-Alg.git
   ```
2. Compila el proyecto (requiere JDK):
   ```sh
   javac -d bin $(find src -name "*.java")
   ```
3. Ejecuta el algoritmo:
   ```sh
   java -cp bin Main.IC2ReactorAlg
   ```
4. Revisa los resultados en `result.txt`.

## 📝 Configuración

Edita el archivo `config.txt` para ajustar parámetros como:

- `POPULATION_SIZE`: Tamaño de la población
- `TOURNAMENT_SIZE`: Tamaño del torneo
- `GENERATIONS`: Número de generaciones
- `FREE_PASS`: Individuos que pasan directo
- `FRESH_BLOOD`: Individuos nuevos por generación
- `MUTATION_CHANCE`: Probabilidad de mutación

## 📖 Ejemplo de uso

```text
POPULATION_SIZE = 100
TOURNAMENT_SIZE = 3
GENERATIONS = 1000
FREE_PASS = 1
FRESH_BLOOD = 15
MUTATION_CHANCE = 70000
```

## 🛠️ Tecnologías

- Java
- Algoritmos evolutivos/genéticos
- Simulación de reactores nucleares

## 📚 Créditos y agradecimientos

- [Miguel González García](https://github.com/miguerubsk) - Autor principal
- Código del simulador: [Ic2ExpReactorPlanner](https://github.com/MauveCloud/Ic2ExpReactorPlanner)

## 📄 Licencia

Este proyecto está bajo la licencia GPLv3. Consulta el archivo `LICENSE.md` para más detalles.

## 💡 Contribuir

¡Las contribuciones son bienvenidas! Por favor, abre un issue o pull request para sugerencias, mejoras o reportar errores.

## 📬 Contacto

Para dudas o sugerencias, abre un [issue](https://github.com/miguerubsk/IC2-Reactor-Alg/issues) o consulta mi perfil para más opciones.

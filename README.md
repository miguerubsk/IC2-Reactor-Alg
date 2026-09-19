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
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/codeql.yml"><img src="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/codeql.yml/badge.svg" alt="CodeQL Advanced"></a>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/build.yml"><img src="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/build.yml/badge.svg" alt="Build"></a>
		<a href="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/run.yml"><img src="https://github.com/miguerubsk/IC2-Reactor-Alg/actions/workflows/run.yml/badge.svg" alt"Algorithm execution"></a>
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
├── src/main/java/io/github/miguerubsk/ic2reactoralg/
│   ├── Main.java           # Punto de entrada
│   ├── genetic/            # Lógica del algoritmo genético
│   └── simulator/          # Simulador de reactores y componentes
├── pom.xml                 # Build con Maven
├── mvnw, mvnw.cmd          # Maven Wrapper (no hace falta instalar Maven)
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
2. Compila el proyecto (requiere JDK 17 o superior; Maven se descarga solo mediante el wrapper):
   ```sh
   ./mvnw package
   ```
3. Ejecuta el algoritmo desde la raíz del repositorio (lee `config.txt` del directorio actual):
   ```sh
   java -jar target/ic2-reactor-alg.jar
   ```
4. Revisa los resultados en `result.txt`.

## 📝 Configuración

Edita el archivo `config.txt` para ajustar parámetros como:

- `POPULATION_SIZE`: Tamaño de la población
- `TOURNAMENT_SIZE`: Tamaño del torneo
- `GENERATIONS`: Número de generaciones (`0` = sin límite)
- `FREE_PASS`: Individuos que pasan directo
- `FRESH_BLOOD`: Individuos nuevos por generación
- `MUTATION_CHANCE`: Probabilidad de mutación, sobre 1 000 000
- `MAX_GENERATIONS_WITHOUT_IMPROVEMENT`: Generaciones sin mejora antes de reiniciar la población

Las claves que falten toman su valor por defecto (100, 3, 1000, 1, 15, 70000 y 50). `FREE_PASS + FRESH_BLOOD` no puede superar `POPULATION_SIZE`.

## 📖 Ejemplo de uso

```text
POPULATION_SIZE = 100
TOURNAMENT_SIZE = 3
GENERATIONS = 1000
FREE_PASS = 1
FRESH_BLOOD = 15
MUTATION_CHANCE = 70000
MAX_GENERATIONS_WITHOUT_IMPROVEMENT = 50
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

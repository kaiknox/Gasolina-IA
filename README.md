# Proyecto Gasolina - Instrucciones de compilación y ejecución

Este repositorio contiene código Java para el problema de distribución de gasolina (paquete `IA.Gasolina`) y dependencias externas en JAR. El `Makefile` incluido facilita compilación y ejecución en sistemas Unix (macOS/Linux) y proporciona targets para Windows.

IMPORTANTE: algunos JAR (por ejemplo `Gasolina.jar`) pueden haber sido compilados con una versión más reciente de Java (p. ej. Java 21). Si al ejecutar obtienes errores como:

```
java.lang.UnsupportedClassVersionError: ... compiled by a more recent Java Runtime (class file version 65.0)
```

entonces debes usar JDK 21 (o recompilar los JAR con tu versión de Java). Más abajo hay instrucciones rápidas para seleccionar/instalar JDK en macOS.

---

Requisitos
- JDK (javac/java) instalado. Se recomienda JDK 21 si los jars fueron compilados con Java 21.
- `make` y utilidades estándar de Unix (bash, tar). En macOS estas están disponibles por defecto o mediante Homebrew.

Resumen de targets útiles en el `Makefile`
- `make compile` — Compila las fuentes Java listadas y deja las clases en `bin/`.
- `make run` — Ejecuta `make compile` y lanza la clase principal indicada (`Main`).
- `make run-jar` — Ejecuta solamente el JAR `Gasolina.jar` con `java -jar`.
- `make clean` — Borra clases compiladas en `bin/`.
- `make check-jar` — Comprueba que los JAR listados existen (saldrá error si faltan).
- `make info` — Muestra variables de proyecto y classpath calculado.
- `make jar-info` — Lista el contenido del JAR principal con `jar -tf`.
- `make build-run` — Combina `clean`, `compile`, `run`.

Windows: el Makefile incluye variantes `compile-win`, `run-win`, `clean-win`, `build-run-win` que construyen/ejecutan con un classpath separado (usa `;` como separador).

Comandos rápidos (macOS / Linux - zsh/bash)

1) Verifica que los JAR necesarios existen

```bash
make check-jar
```

2) Compilar

```bash
make compile
```

3) Ejecutar (después de compilar)

```bash
make run
```

4) Limpiar archivos compilados

```bash
make clean
```

Si quieres compilar y ejecutar en un solo paso:

```bash
make build-run
```

Uso en Windows (PowerShell / cmd)

```powershell
# desde PowerShell
make compile-win
make run-win
```

Nota sobre la versión de Java y solución al UnsupportedClassVersionError
- Opción A (fácil): instalar JDK 21 y usarlo para compilar/ejecutar.
  - macOS (Homebrew):

```bash
brew install --cask temurin21
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
javac -version
```

  - Alternativamente, usar `sdkman` o la distribución de JDK que prefieras.

- Opción B: recompilar `Gasolina.jar` (si tienes el código fuente) con tu JDK actual.
- Opción C: ejecutar `java` apuntando a un JDK instalado explícitamente (ruta completa a `java` y `javac`).

Registro de salida y depuración
- Si el programa imprime mucho por `System.out` durante la búsqueda, redirige la salida a un archivo para analizarla:

```bash
make run > salida.txt 2>&1
```

- Para inspeccionar el contenido del JAR si quieres ver las clases y versiones:

```bash
make jar-info
```

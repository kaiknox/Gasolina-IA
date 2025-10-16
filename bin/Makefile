# Makefile para proyecto Java con Gasolina.jar
# Autor: Generado automáticamente
# Fecha: $(shell date)

# Variables
JAVA_FILES = Main.java IA/Gasolina/*.java
JAR_FILE = Gasolina.jar
MAIN_CLASS = Main

# JAR libraries: auto-detect .jar files in the project root so the
# Makefile knows which libraries belong to the project. You can still
# add explicit jars if needed; by default we pick up all *.jar here.
JAR_LIBS := $(wildcard *.jar)
# Ensure Gasolina.jar is present in the list and prefer it last (optional)
ifeq (,$(filter $(JAR_FILE),$(JAR_LIBS)))
JAR_LIBS += $(JAR_FILE)
endif

# Default: Unix-style (keeps behaviour for Linux users)
SEP = :
RM = rm -f

# Windows-specific variables (explicit targets below)
SEP_WIN = ;
RM_WIN = del /Q

# Build CLASSPATH for Unix and Windows
empty :=
space := $(empty) $(empty)
CLASSPATH = .$(SEP)$(subst $(space),$(SEP),$(JAR_LIBS))
CLASSPATH_WIN = .$(SEP_WIN)$(subst $(space),$(SEP_WIN),$(JAR_LIBS))

# Compilador y flags
JAVAC = javac
JAVA = java
JAVAC_FLAGS = -cp $(CLASSPATH)
JAVA_FLAGS = -cp $(CLASSPATH)

# Regla por defecto
all: compile

# Compilar el proyecto (compila todos los .java listados en JAVA_FILES)
compile:
	@echo "Compilando archivos Java..."
	$(JAVAC) $(JAVAC_FLAGS) $(JAVA_FILES)
	@echo "Compilación completada."

# Compilar en Windows (usar CLASSPATH_WIN)
# Nota: no dependemos de $(CLASS_FILES) porque la regla por defecto
# usa $(JAVAC_FLAGS) (CLASSPATH Unix). Aquí compilamos explícitamente
# con el classpath de Windows para evitar que Make ejecute la regla Unix.
compile-win:
	@echo "Compilando archivos Java (Windows)..."
	$(JAVAC) -cp $(CLASSPATH_WIN) $(JAVA_FILES)
	@echo "Compilación completada."

# Ejecutar el programa principal
run: compile
	@echo "Ejecutando $(MAIN_CLASS)..."
	$(JAVA) $(JAVA_FLAGS) $(MAIN_CLASS)

# Windows: ejecutar desde PowerShell/cmd. Usa CLASSPATH_WIN
run-win: compile-win
	@echo "Ejecutando $(MAIN_CLASS) en Windows..."
	$(JAVA) -cp $(CLASSPATH_WIN) $(MAIN_CLASS)

# Ejecutar solo el JAR (para pruebas)
run-jar:
	@echo "Ejecutando $(JAR_FILE)..."
	$(JAVA) -jar $(JAR_FILE)

# Limpiar archivos compilados
clean:
	@echo "Limpiando archivos compilados..."
	$(RM) *.class 2>nul || true
	@echo "Limpieza completada."

# Limpiar en Windows (PowerShell/cmd)
clean-win:
	@echo "Limpiando archivos compilados (Windows)..."
	$(RM_WIN) *.class 2>nul || true
	@echo "Limpieza completada."

# Verificar que el JAR existe
check-jar:
	@echo "Verificando jars listados en JAR_LIBS: $(JAR_LIBS)"
	@missing=0; \
	for j in $(JAR_LIBS); do \
		if [ ! -f "$$j" ]; then \
			echo "Error: $$j no encontrado"; missing=1; \
		else \
			echo "$$j encontrado"; \
		fi; \
	done; \
	if [ $$missing -eq 1 ]; then exit 1; fi

# Windows check-jar (PowerShell/cmd)
check-jar-win:
	@echo "Verificando jars (Windows): $(JAR_LIBS)"
	@set missing=0 & for %%j in ($(JAR_LIBS)) do @if not exist "%%j" (echo Error: %%j no encontrado & set missing=1) else @echo %%j encontrado
	@if %missing%==1 exit 1

# Mostrar información del proyecto
info:
	@echo "=== INFORMACIÓN DEL PROYECTO ==="
	@echo "Archivos Java: $(JAVA_FILES)"
	@echo "JAR externo: $(JAR_FILE)"
	@echo "Clase principal: $(MAIN_CLASS)"
	@echo "Classpath: $(CLASSPATH)"
	@echo "==============================="

# Mostrar contenido del JAR
jar-info:
	@echo "Contenido de $(JAR_FILE):"
	jar -tf $(JAR_FILE)

# Compilar y ejecutar en un solo paso
build-run: clean compile run

# Windows combined target
build-run-win: clean-win compile-win run-win

# Verificar la estructura del proyecto
check-structure:
	@echo "Verificando estructura del proyecto..."
	@bash -c 'ls -la *.java' 2>/dev/null || (echo "Advertencia: No se encontraron archivos .java" )
	@for j in $(JAR_LIBS); do ls -la $$j 2>/dev/null || echo "Error: $$j no encontrado"; done
	@bash -c 'ls -la javadoc/' 2>/dev/null && echo "Documentación Javadoc encontrada" || echo "Sin documentación Javadoc"

# Crear backup del código fuente
backup:
	@echo "Creando backup..."
	@bash -c 'tar -czf backup-$(shell date +%Y%m%d-%H%M%S).tar.gz *.java $(JAR_LIBS) Makefile' 2>/dev/null || echo "tar no disponible: omitiendo backup con tar"
	@echo "Backup (intento) completado."

# Ayuda
help:
	@echo "=== COMANDOS DISPONIBLES ==="
	@echo "make compile     - Compilar archivos Java"
	@echo "make run         - Compilar y ejecutar el programa"
	@echo "make run-jar     - Ejecutar solo el JAR de Gasolina"
	@echo "make clean       - Limpiar archivos compilados"
	@echo "make check-jar   - Verificar que el JAR existe"
	@echo "make info        - Mostrar información del proyecto"
	@echo "make jar-info    - Mostrar contenido del JAR"
	@echo "make build-run   - Limpiar, compilar y ejecutar"
	@echo "make check-structure - Verificar archivos del proyecto"
	@echo "make backup      - Crear backup del código fuente"
	@echo "---- Windows-specific (PowerShell/cmd) ----"
	@echo "make compile-win - Compilar usando classpath Windows"
	@echo "make run-win     - Ejecutar usando classpath Windows"
	@echo "make clean-win   - Limpiar (Windows)"
	@echo "make build-run-win - Limpiar, compilar y ejecutar (Windows)"
	@echo "make help        - Mostrar esta ayuda"
	@echo "=========================="

# Reglas que no son archivos
.PHONY: all compile run run-jar clean check-jar info jar-info build-run check-structure backup help
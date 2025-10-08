# Makefile para proyecto Java con Gasolina.jar
# Autor: Generado automáticamente
# Fecha: $(shell date)

# Variables
JAVA_FILES = Main.java
JAR_FILE = Gasolina.jar
MAIN_CLASS = Main
CLASSPATH = .:$(JAR_FILE)

# Compilador y flags
JAVAC = javac
JAVA = java
JAVAC_FLAGS = -cp $(CLASSPATH)
JAVA_FLAGS = -cp $(CLASSPATH)

# Archivos generados
CLASS_FILES = $(JAVA_FILES:.java=.class)

# Regla por defecto
all: compile

# Compilar el proyecto
compile: $(CLASS_FILES)

$(CLASS_FILES): $(JAVA_FILES)
	@echo "Compilando archivos Java..."
	$(JAVAC) $(JAVAC_FLAGS) $(JAVA_FILES)
	@echo "Compilación completada."

# Ejecutar el programa principal
run: compile
	@echo "Ejecutando $(MAIN_CLASS)..."
	$(JAVA) $(JAVA_FLAGS) $(MAIN_CLASS)

# Ejecutar solo el JAR (para pruebas)
run-jar:
	@echo "Ejecutando $(JAR_FILE)..."
	$(JAVA) -jar $(JAR_FILE)

# Limpiar archivos compilados
clean:
	@echo "Limpiando archivos compilados..."
	rm -f *.class
	@echo "Limpieza completada."

# Verificar que el JAR existe
check-jar:
	@if [ ! -f $(JAR_FILE) ]; then \
		echo "Error: $(JAR_FILE) no encontrado en el directorio actual"; \
		exit 1; \
	else \
		echo "$(JAR_FILE) encontrado correctamente"; \
	fi

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

# Verificar la estructura del proyecto
check-structure:
	@echo "Verificando estructura del proyecto..."
	@ls -la *.java 2>/dev/null || echo "Advertencia: No se encontraron archivos .java"
	@ls -la $(JAR_FILE) 2>/dev/null || echo "Error: $(JAR_FILE) no encontrado"
	@ls -la javadoc/ 2>/dev/null && echo "Documentación Javadoc encontrada" || echo "Sin documentación Javadoc"

# Crear backup del código fuente
backup:
	@echo "Creando backup..."
	tar -czf backup-$(shell date +%Y%m%d-%H%M%S).tar.gz *.java $(JAR_FILE) Makefile
	@echo "Backup creado."

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
	@echo "make help        - Mostrar esta ayuda"
	@echo "=========================="

# Reglas que no son archivos
.PHONY: all compile run run-jar clean check-jar info jar-info build-run check-structure backup help
# Makefile simple para proyecto Gasolina-IA

# Variables
JAVAC = javac
JAVA = java
BIN = bin
CLASSPATH = $(BIN):AIMA.jar:Gasolina.jar:.

# Fuentes
SOURCES = Main.java IA/Gasolina/*.java

# Target por defecto
all: compile

# Compilar
compile:
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) -cp "AIMA.jar:Gasolina.jar:." $(SOURCES)

# Ejecutar
run: compile
	$(JAVA) -cp "$(CLASSPATH)" Main

# Limpiar
clean:
	rm -rf $(BIN)

# Copia de seguridad
backup:
	tar -czf backup-$(shell date +%Y%m%d-%H%M%S).tar.gz Main.java IA/Gasolina/*.java AIMA.jar Gasolina.jar Makefile
	@echo "Backup creado ✓"

# Ayuda
help:
	@echo "Comandos disponibles:"
	@echo "  make compile  - Compilar el proyecto"
	@echo "  make run      - Compilar y ejecutar"
	@echo "  make clean    - Limpiar archivos compilados"
	@echo "  make backup   - Crear copia de seguridad"
	@echo "  make help     - Mostrar esta ayuda"

.PHONY: all compile run clean backup help

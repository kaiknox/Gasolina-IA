# Sistema de Gestión de Gasolineras

Este proyecto demuestra el uso de las clases del paquete `IA.Gasolina` proporcionadas en `Gasolinera.jar`.

## Compilación

Para compilar el programa, ejecuta el siguiente comando en la terminal:

```bash
javac -cp Gasolinera.jar Main.java
```

## Ejecución

Para ejecutar el programa, usa el siguiente comando:

```bash
java -cp .:Gasolinera.jar Main
```

**Nota para Windows:** En Windows, usa punto y coma (`;`) en lugar de dos puntos (`:`):
```bash
java -cp .;Gasolinera.jar Main
```

## Descripción del Programa

El programa `Main.java` demuestra el uso de las siguientes clases:

### 1. **Gasolineras**
- Crea una lista de gasolineras con coordenadas aleatorias
- Constructor: `Gasolineras(int numGasolineras, int seed)`

### 2. **Gasolinera**
- Representa una gasolinera individual con:
  - Coordenadas X e Y
  - Lista de peticiones pendientes
- Métodos principales:
  - `getCoordX()` / `setCoordX(int x)`
  - `getCoordY()` / `setCoordY(int y)`
  - `getPeticiones()` / `setPeticiones(ArrayList<Integer>)`

### 3. **CentrosDistribucion**
- Crea una lista de centros de distribución
- Constructor: `CentrosDistribucion(int numCentros, int multiplicidad, int seed)`

### 4. **Distribucion**
- Representa un centro de distribución con coordenadas
- Métodos principales:
  - `getCoordX()` / `setCoordX(int x)`
  - `getCoordY()` / `setCoordY(int y)`

## Funcionalidades del Programa

1. **Creación de Gasolineras**: Genera 10 gasolineras con coordenadas aleatorias
2. **Creación de Centros de Distribución**: Genera 3 centros con 2 camiones cada uno
3. **Visualización de Datos**: Muestra información detallada de cada elemento
4. **Modificación de Datos**: Demuestra cómo modificar coordenadas y peticiones
5. **Estadísticas**: Calcula y muestra estadísticas básicas del sistema

## Ejemplo de Salida

```
=== Sistema de Gestión de Gasolineras ===

Se han generado 10 gasolineras:
--------------------------------------------------
Gasolinera 1:
  Coordenadas: (X, Y)
  Peticiones pendientes: [...]

...

=== Centros de Distribución ===
...

=== Estadísticas ===
Total de gasolineras: 10
Total de peticiones: XX
Promedio de peticiones por gasolinera: X.XX
...
```

## Notas

- El parámetro `seed` asegura que los datos generados sean reproducibles
- La multiplicidad en los centros de distribución simula tener múltiples camiones en la misma ubicación

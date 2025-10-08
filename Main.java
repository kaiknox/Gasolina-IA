import IA.Gasolina.*;

public class Main {
    public static void main(String[] args) {
        // Ejemplo de uso de las clases del JAR
        
        // 1. Crear una lista de gasolineras
        // Constructor: Gasolineras(int ngas, int seed)
        // ngas = número de gasolineras, seed = semilla aleatoria
        Gasolineras gasolineras = new Gasolineras(10, 42);

        System.out.println("polla");
        
        System.out.println("=== INFORMACIÓN DE GASOLINERAS ===");
        System.out.println("Número de gasolineras: " + gasolineras.size());
        
        // 2. Recorrer las gasolineras y mostrar información
        for (int i = 0; i < gasolineras.size(); i++) {
            Gasolinera gas = gasolineras.get(i);
            System.out.println("Gasolinera " + i + ": (" + 
                             gas.getCoordX() + ", " + gas.getCoordY() + ")");
            
            // Mostrar peticiones de cada gasolinera
            if (gas.getPeticiones().size() > 0) {
                for (int j = 0; j < gas.getPeticiones().size(); j++) {
                    System.out.println("  Petición " + j + ": " + 
                                     gas.getPeticiones().get(j) + " días");
                }
            } else {
                System.out.println("  -> Sin peticiones <-");
            }
        }
        
        // 3. Crear centros de distribución
        // Constructor: CentrosDistribucion(int ncen, int mult, int seed)
        // ncen = número de centros, mult = multiplicidad, seed = semilla
        CentrosDistribucion centros = new CentrosDistribucion(5, 1, 123);
        
        System.out.println("\n=== INFORMACIÓN DE CENTROS DE DISTRIBUCIÓN ===");
        System.out.println("Número de centros: " + centros.size());
        
        for (int i = 0; i < centros.size(); i++) {
            Distribucion centro = centros.get(i);
            System.out.println("Centro " + i + ": (" + 
                             centro.getCoordX() + ", " + centro.getCoordY() + ")");
        }
        
        // 4. Ejemplo de manipulación de datos
        System.out.println("\n=== EJEMPLO DE MANIPULACIÓN ===");
        if (gasolineras.size() > 0) {
            Gasolinera primeraGas = gasolineras.get(0);
            System.out.println("Primera gasolinera original: (" + 
                             primeraGas.getCoordX() + ", " + primeraGas.getCoordY() + ")");
            
            // Modificar coordenadas
            primeraGas.setCoordX(100);
            primeraGas.setCoordY(200);
            System.out.println("Primera gasolinera modificada: (" + 
                             primeraGas.getCoordX() + ", " + primeraGas.getCoordY() + ")");
        }
    }
}
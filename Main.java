import IA.Gasolina.*;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestión de Gasolineras ===\n");
        
        // Crear gasolineras con 10 estaciones y semilla 12345
        int numGasolineras = 10;
        int seed = 12345;
        Gasolineras gasolineras = new Gasolineras(numGasolineras, seed);
        
        System.out.println("Se han generado " + gasolineras.size() + " gasolineras:");
        System.out.println("-".repeat(50));
        
        // Mostrar información de cada gasolinera
        for (int i = 0; i < gasolineras.size(); i++) {
            Gasolinera gas = gasolineras.get(i);
            System.out.println("Gasolinera " + (i + 1) + ":");
            System.out.println("  Coordenadas: (" + gas.getCoordX() + ", " + gas.getCoordY() + ")");
            System.out.println("  Peticiones pendientes: " + gas.getPeticiones());
            System.out.println();
        }
        
        // Crear centros de distribución
        int numCentros = 3;
        int multiplicidad = 2;  // 2 camiones por centro
        CentrosDistribucion centros = new CentrosDistribucion(numCentros, multiplicidad, seed);
        
        System.out.println("\n=== Centros de Distribución ===");
        System.out.println("Se han generado " + centros.size() + " centros de distribución:");
        System.out.println("-".repeat(50));
        
        // Mostrar información de cada centro
        for (int i = 0; i < centros.size(); i++) {
            Distribucion centro = centros.get(i);
            System.out.println("Centro " + (i + 1) + ":");
            System.out.println("  Coordenadas: (" + centro.getCoordX() + ", " + centro.getCoordY() + ")");
        }
        
        // Ejemplo de modificación de datos
        System.out.println("\n=== Modificando datos de ejemplo ===");
        if (gasolineras.size() > 0) {
            Gasolinera primeraGasolinera = gasolineras.get(0);
            System.out.println("Modificando la primera gasolinera...");
            System.out.println("  Coordenadas originales: (" + primeraGasolinera.getCoordX() + 
                             ", " + primeraGasolinera.getCoordY() + ")");
            
            primeraGasolinera.setCoordX(100);
            primeraGasolinera.setCoordY(200);
            
            System.out.println("  Nuevas coordenadas: (" + primeraGasolinera.getCoordX() + 
                             ", " + primeraGasolinera.getCoordY() + ")");
            
            // Modificar peticiones
            ArrayList<Integer> nuevasPeticiones = new ArrayList<>();
            nuevasPeticiones.add(0);
            nuevasPeticiones.add(1);
            nuevasPeticiones.add(3);
            primeraGasolinera.setPeticiones(nuevasPeticiones);
            System.out.println("  Nuevas peticiones: " + primeraGasolinera.getPeticiones());
        }
        
        // Estadísticas
        System.out.println("\n=== Estadísticas ===");
        calcularEstadisticas(gasolineras);
        
        System.out.println("\n=== Fin del programa ===");
    }
    
    /**
     * Calcula y muestra estadísticas básicas de las gasolineras
     */
    private static void calcularEstadisticas(Gasolineras gasolineras) {
        if (gasolineras.isEmpty()) {
            System.out.println("No hay gasolineras para calcular estadísticas.");
            return;
        }
        
        int totalPeticiones = 0;
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        
        for (Gasolinera gas : gasolineras) {
            totalPeticiones += gas.getPeticiones().size();
            
            int x = gas.getCoordX();
            int y = gas.getCoordY();
            
            maxX = Math.max(maxX, x);
            minX = Math.min(minX, x);
            maxY = Math.max(maxY, y);
            minY = Math.min(minY, y);
        }
        
        double promedioPeticiones = (double) totalPeticiones / gasolineras.size();
        
        System.out.println("Total de gasolineras: " + gasolineras.size());
        System.out.println("Total de peticiones: " + totalPeticiones);
        System.out.println("Promedio de peticiones por gasolinera: " + 
                         String.format("%.2f", promedioPeticiones));
        System.out.println("Rango de coordenadas X: [" + minX + ", " + maxX + "]");
        System.out.println("Rango de coordenadas Y: [" + minY + ", " + maxY + "]");
    }
}

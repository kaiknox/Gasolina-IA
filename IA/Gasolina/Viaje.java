package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Viaje {
    
    public List<Integer> indicesGasolineras;     // Índices en Main.gasolineras
    public List<Integer> indicesPeticiones;      // Índices en getPeticiones() de cada gasolinera
    public double distanciaTotal;

    // En la clase Viaje, lo que se guarda son referencias a las peticiones que ese viaje va a atender.
    // Como las gasolineras y sus peticiones ya están en Main.gasolineras, solo necesitas saber:

    // indicesGasolineras: qué gasolineras va a visitar el camión en ese viaje (por índice en la lista global).
    // indicesPeticiones: qué petición concreta de cada gasolinera va a atender (por índice en el ArrayList<Integer> de peticiones de esa gasolinera).

    public Viaje() {
        this.indicesGasolineras = new ArrayList<>();
        this.indicesPeticiones = new ArrayList<>();
        this.distanciaTotal = 0.0;
    }

    // Métodos útiles
    // Añadir petición, calcular distancia, etc.
}

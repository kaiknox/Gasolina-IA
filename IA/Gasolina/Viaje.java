package IA.Gasolina;

public class Viaje {

    private int coordX_inicio;
    private int coordY_inicio;
    private int coordX_fin;
    private int coordY_fin;

    private double distanciaTotal;
    private double tiempoTotal;

    // En la clase Viaje, lo que se guarda son referencias a las peticiones que ese viaje va a atender.
    // Como las gasolineras y sus peticiones ya están en Main.gasolineras, solo necesitas saber:

    // indicesGasolineras: qué gasolineras va a visitar el camión en ese viaje (por índice en la lista global).
    // indicesPeticiones: qué petición concreta de cada gasolinera va a atender (por índice en el ArrayList<Integer> de peticiones de esa gasolinera).

    public Viaje() {
        coordX_inicio = 0;
        coordY_inicio = 0;
        coordX_fin = 0;
        coordY_fin = 0;
        this.distanciaTotal = 0;
        this.tiempoTotal = 0;
    }

    public Viaje(int x1, int y1, int x2, int y2) {
        this.coordX_inicio = x1;
        this.coordY_inicio = y1;
        this.coordX_fin = x2;
        this.coordY_fin = y2;
        this.distanciaTotal = distancia(x1, y1, x2, y2);
        this.tiempoTotal = tiempo(x1, y1, x2, y2);
    }

    public int getDiasPendientes() {
        // No sé cómo implementarlo ahora mismo pero habría que saber los días pendientes de la petición para calcular el beneficio.
        return 0; // Placeholder
    }

    // Métodos útiles
    // Añadir petición, calcular distancia, etc.

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public double getTiempoTotal() {
        return tiempoTotal;
    }

    private double distancia(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double tiempo(int x1, int y1, int x2, int y2) {
        double distancia = distancia(x1, y1, x2, y2);
        return distancia / 80.0; // Asumiendo una velocidad media de 80 km/h
    }

    public double getCantidad() {
        return (1000*((100-Math.pow(2,getDiasPendientes()))/100))-(2*distanciaTotal);
    }
}

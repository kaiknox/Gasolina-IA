package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Viajes {
    private static final int MAX_VIAJES = 2; // máximo número de Viaje dentro de un objeto Viajes
    private List<Viaje> listaViajes = new ArrayList<>(MAX_VIAJES);
    private double distanciaTotal;
    private double tiempoTotal;

    public Viajes() {
        this.listaViajes = new ArrayList<>(MAX_VIAJES);
        this.distanciaTotal = 0.0;
        this.tiempoTotal = 0.0;
    }

    public Viajes(List<Viaje> viajes) {
        this.listaViajes = new ArrayList<>(viajes);
        this.distanciaTotal = 0.0;
        this.tiempoTotal = 0.0;
        for (Viaje viaje : listaViajes) {
            this.distanciaTotal += viaje.getDistanciaTotal();
            this.tiempoTotal += viaje.getTiempoTotal();
        }
    }
    // Métodos para añadir viajes, calcular distancia total, etc.
    /** Añade un viaje si no excede el límite; lanza IllegalStateException en caso contrario. */
    public void añadirViaje(Viaje viaje) {
        if (!puedeAñadir()) {
            throw new IllegalStateException("No se pueden añadir más de " + MAX_VIAJES + " viajes a este objeto Viajes");
        }
        listaViajes.add(viaje);
        distanciaTotal += viaje.getDistanciaTotal();
        tiempoTotal += viaje.getTiempoTotal();
    }

    /** Comprueba si se puede añadir otro Viaje sin exceder el límite. */
    public boolean puedeAñadir() {
        return listaViajes.size() < MAX_VIAJES;
    }
    public double getDistanciaTotal() {
        return distanciaTotal;
    }
    public double getTiempoTotal() {
        return tiempoTotal;
    }
    public List<Viaje> getListaViajes() {
        return listaViajes;
    }

    public double getCantidad(){
        double cantidadTotal = 0;
        for (int i = 0; i < listaViajes.size(); i++) {
            cantidadTotal += listaViajes.get(i).getCantidad();
        }
        return cantidadTotal;
    }
}

package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Viajes {
    private List<Viaje> listaViajes;
    private double distanciaTotal;
    private double tiempoTotal;

    public Viajes() {
        this.listaViajes = new ArrayList<>();
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
    public void añadirViaje(Viaje viaje) {
        listaViajes.add(viaje);
        distanciaTotal += viaje.getDistanciaTotal();
        tiempoTotal += viaje.getTiempoTotal();

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

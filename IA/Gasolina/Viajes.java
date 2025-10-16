package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Viajes {
    private static final int MAX_VIAJES = 3; // máximo número de Viaje dentro de un objeto Viajes (3 tramos: salida, intermedio, retorno)
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
    /**
     * Añade un viaje respetando la secuencia de 3 tramos:
     *  - tramo 0: centro (camion) -> gasolinera
     *  - tramo 1: gasolinera -> gasolinera
     *  - tramo 2: gasolinera -> centro (camion)
     * Lanza IllegalStateException si no hay espacio o IllegalArgumentException si el tramo no respeta la secuencia.
     */
    public void añadirViaje(Viaje viaje, Camion camion) {
        if (!puedeAñadir()) {
            throw new IllegalStateException("No se pueden añadir más de " + MAX_VIAJES + " viajes a este objeto Viajes");
        }
        int size = listaViajes.size();
        if (size == 0) {
            // Primer tramo: debe empezar en el centro (coordenadas del camión)
            if (viaje.getCoordX_inicio() != camion.getCoordX() || viaje.getCoordY_inicio() != camion.getCoordY()) {
                throw new IllegalArgumentException("Primer tramo debe comenzar en el centro del camión");
            }
        } else if (size == 1) {
            // Segundo tramo: debe empezar donde terminó el anterior (gasolinera)
            Viaje anterior = listaViajes.get(size - 1);
            if (viaje.getCoordX_inicio() != anterior.getCoordX_fin() || viaje.getCoordY_inicio() != anterior.getCoordY_fin()) {
                throw new IllegalArgumentException("Segundo tramo debe empezar en la gasolinera del tramo anterior");
            }
        } else if (size == 2) {
            // Tercer tramo: debe empezar donde terminó el anterior y terminar en el centro
            Viaje anterior = listaViajes.get(size - 1);
            if (viaje.getCoordX_inicio() != anterior.getCoordX_fin() || viaje.getCoordY_inicio() != anterior.getCoordY_fin()) {
                throw new IllegalArgumentException("Tercer tramo debe empezar en la gasolinera del tramo anterior");
            }
            if (viaje.getCoordX_fin() != camion.getCoordX() || viaje.getCoordY_fin() != camion.getCoordY()) {
                throw new IllegalArgumentException("Tercer tramo debe terminar en el centro del camión");
            }
        }

        // If adding the first real leg (size==0 before adding), also append a provisional return from that gasolinera to the camion's center.
        int before = listaViajes.size();
        listaViajes.add(viaje);
        distanciaTotal += viaje.getDistanciaTotal();
        tiempoTotal += viaje.getTiempoTotal();

        if (before == 0) {
            // add provisional return: from this.gasolinera (viaje end) back to camion center
            Viaje provisional = new Viaje(viaje.getCoordX_fin(), viaje.getCoordY_fin(), camion.getCoordX(), camion.getCoordY(), viaje.getDiasPendientes(), true);
            listaViajes.add(provisional);
            distanciaTotal += provisional.getDistanciaTotal();
            tiempoTotal += provisional.getTiempoTotal();
        } else if (before == 1) {
            // We previously had added a provisional return as the second entry. Now we're adding the real intermediate leg.
            // Replace the provisional (which should be at index 1) with the new real leg, and append a final return to center based on the new last gasolinera.
            if (listaViajes.size() >= 2) {
                Viaje maybeProvisional = listaViajes.get(1);
                if (maybeProvisional.isProvisionalReturn()) {
                    // remove provisional totals
                    distanciaTotal -= maybeProvisional.getDistanciaTotal();
                    tiempoTotal -= maybeProvisional.getTiempoTotal();
                    listaViajes.set(1, viaje); // replace provisional with real intermediate
                    // add final return from this new viaje end back to center
                    Viaje finalReturn = new Viaje(viaje.getCoordX_fin(), viaje.getCoordY_fin(), camion.getCoordX(), camion.getCoordY(), viaje.getDiasPendientes(), false);
                    listaViajes.add(finalReturn);
                    distanciaTotal += finalReturn.getDistanciaTotal();
                    tiempoTotal += finalReturn.getTiempoTotal();
                }
            }
        }
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

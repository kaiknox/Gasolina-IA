package IA.Gasolina;

import java.util.List;
import IA.Gasolina.Gasolinera;
import IA.Gasolina.Distribucion;
/**
 * Board implementation moved/renamed from ProbIA5Board
 */
public class GasolinaBoard {
    /* Class independent from AIMA classes
       - It has to implement the state of the problem and its operators
     *

    /* State data structure
        vector with the parity of the coins (we can assume 0 = heads, 1 = tails
     */

    private Estado estado_actual;
    private List<Gasolinera> gasolineras;
    private List<Distribucion> centros;

    /* Constructor */
    public GasolinaBoard(Estado estado_inicial, java.util.List<Gasolinera> gasolineras, java.util.List<Distribucion> centros) {
        this.estado_actual = estado_inicial;
        this.gasolineras = gasolineras;
        this.centros = centros;
    }

    /* OPERADORES */
    public void moverCamion(int idCamion, int nuevaX, int nuevaY) {
        Camion camion = estado_actual.getCamiones().get(idCamion);
        camion.setCoordX(nuevaX);
        camion.setCoordY(nuevaY);
    }

    // Reasigna un viaje de un camion a otro
    public void reasignarViajes(int idCamionOrigen, int idCamionDestino, int idViaje) {
        Camion camionOrigen = estado_actual.getCamiones().get(idCamionOrigen);
        Camion camionDestino = estado_actual.getCamiones().get(idCamionDestino);

        // Hay que buscar la peticion en el camion origen y eliminarla
        // Luego añadirla al camion destino
        List<Viajes> viajesOrigen = camionOrigen.getViajes();
        List<Viajes> viajesDestino = camionDestino.getViajes();

        Viajes viajeARemover = viajesOrigen.get(idViaje);

        if (viajeARemover != null) {
            viajesOrigen.remove(viajeARemover);
            camionOrigen.setDistanciaRecorrida(camionOrigen.getDistanciaRecorrida() - viajeARemover.getDistanciaTotal());
            camionOrigen.setHorasTrabajadas(camionOrigen.getHorasTrabajadas() - viajeARemover.getTiempoTotal());
            viajesDestino.add(viajeARemover);
        }
    }

    // Intercambia dos viajes entre dos camiones
    public void intercambiaViajes(int idCamionA, int idCamionB, int idViajeA, int idViajeB) {
        Camion camionA = estado_actual.getCamiones().get(idCamionA);
        Camion camionB = estado_actual.getCamiones().get(idCamionB);

        List<Viajes> viajesOrigen = camionA.getViajes();
        List<Viajes> viajesDestino = camionB.getViajes();

        Viajes viajeA = viajesOrigen.get(idViajeA);
        Viajes viajeB = viajesDestino.get(idViajeB);

        if (viajeA != null && viajeB != null ) {
            viajesOrigen.remove(viajeA);
            camionA.setDistanciaRecorrida(camionA.getDistanciaRecorrida() - viajeA.getDistanciaTotal() + viajeB.getDistanciaTotal());
            camionA.setHorasTrabajadas(camionA.getHorasTrabajadas() - viajeA.getTiempoTotal() + viajeB.getTiempoTotal());
            viajesDestino.remove(viajeB);
            camionB.setDistanciaRecorrida(camionB.getDistanciaRecorrida() - viajeB.getDistanciaTotal() + viajeA.getDistanciaTotal());
            camionB.setHorasTrabajadas(camionB.getHorasTrabajadas() - viajeB.getTiempoTotal() + viajeA.getTiempoTotal());
            viajesOrigen.add(viajeB);
            viajesDestino.add(viajeA);
        }

        
    }

    /* Getters and setters */
    public Estado getEstado_actual() {
        return estado_actual;
    }

    /* Heuristic function */

    public double heuristic(){
        List<Camion> camiones = estado_actual.getCamiones();
        double beneficioTotal = 0.0;
        for (int i=0; i<camiones.size(); i++){
            beneficioTotal += camiones.get(i).getBeneficio();
        }
        return beneficioTotal;
    }

    /* Goal test */

     public boolean is_goal(){ return true; } // --------------- no se si hace falta

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state






    public void crearEstadoInicial(int funcionAescoger) {
        if(funcionAescoger == 1) {
            crearEstadoInicial1();
        }
        else if (funcionAescoger == 2) {
            crearEstadoInicial2();
        }
    }

    private void crearEstadoInicial1() {
        // Strategy 1: assign each gas station request to the nearest truck
        // Assumptions:
        // - There is a global list `Main.gasolineras` accessible which contains Gasolinera objects
        // - Each Gasolinera has getPeticiones() which returns ArrayList<Integer> (days pending)
        // - Each Camion has coordinates and addPeticion(Peticion) method

        // Defensive checks
        if (gasolineras == null || gasolineras.isEmpty() || estado_actual.getCamiones() == null || estado_actual.getCamiones().isEmpty()) return;

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
            if (peticiones == null) continue;

            for (int d = 0; d < peticiones.size(); d++) {
                // find nearest camion
                int bestCamion = -1;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < estado_actual.getCamiones().size(); c++) {
                    Camion camion = estado_actual.getCamiones().get(c);
                    double dist = distancia(camion.getCoordX(), camion.getCoordY(), g.getCoordX(), g.getCoordY());
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCamion = c;
                    }
                }

                if (bestCamion >= 0) {
                    estado_actual.getCamiones().get(bestCamion).addPeticion(gIndex, d);
                }
            }
        }
    }

    private void crearEstadoInicial2() {
        // Strategy 2: round-robin assign requests across trucks
        if (gasolineras == null || gasolineras.isEmpty() || camiones == null || camiones.isEmpty()) return;

        int camionIndex = 0;
        int nCamiones = camiones.size();

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
            if (peticiones == null) continue;

            for (int d = 0; d < peticiones.size(); d++) {
                camiones.get(camionIndex).addPeticion(gIndex, d);
                camionIndex = (camionIndex + 1) % nCamiones;
            }
        }
    }





    /* ^^^^^ TO COMPLETE ^^^^^ */
    public void crearEstadoInicial(int funcionAescoger) {
        if(funcionAescoger == 1) {
            crearEstadoInicial1();
        }
        else if (funcionAescoger == 2) {
            crearEstadoInicial2();
        }
    }

    private void crearEstadoInicial1() {
        // Strategy 1: assign each gas station request to the nearest truck
        // Assumptions:
        // - There is a global list `Main.gasolineras` accessible which contains Gasolinera objects
        // - Each Gasolinera has getPeticiones() which returns ArrayList<Integer> (days pending)
        // - Each Camion has coordinates and addPeticion(Peticion) method

        // Defensive checks
        if (gasolineras == null || gasolineras.isEmpty() || estado_actual.getCamiones() == null || estado_actual.getCamiones().isEmpty()) return;

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
            if (peticiones == null) continue;

            for (int d = 0; d < peticiones.size(); d++) {
                // find nearest camion
                int bestCamion = -1;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < estado_actual.getCamiones().size(); c++) {
                    Camion camion = estado_actual.getCamiones().get(c);
                    double dist = distancia(camion.getCoordX(), camion.getCoordY(), g.getCoordX(), g.getCoordY());
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCamion = c;
                    }
                }

                if (bestCamion >= 0) {
                    estado_actual.getCamiones().get(bestCamion).addPeticion(g, d);
                }
            }
        }
    }

    private void crearEstadoInicial2() {
        // Strategy 2: round-robin assign requests across trucks
        List<Camion> camiones = estado_actual.getCamiones();
        if (gasolineras == null || gasolineras.isEmpty() || camiones == null || camiones.isEmpty()) return;

        int camionIndex = 0;
        int nCamiones = camiones.size();

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
            if (peticiones == null) continue;

            for (int d = 0; d < peticiones.size(); d++) {
                camiones.get(camionIndex).addPeticion(g, d);
                camionIndex = (camionIndex + 1) % nCamiones;
            }
        }
    }

    private double distancia(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public List<Gasolinera> getGasolineras() {
        return gasolineras;
    }

    public List<Distribucion> getCentros() {
        return centros;
    }
}

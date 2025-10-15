package IA.Gasolina;

import java.util.List;
import java.util.ArrayList;

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
    private List<Distribucion> centrosDistribucion;

    /* Constructor */
    public GasolinaBoard(Estado estado_inicial) {

        this.estado_actual = estado_inicial;
    }

    /* OPERADORES */
    public void moverCamion(int idCamion, int nuevaX, int nuevaY) {
        Camion camion = estado_actual.getCamiones().get(idCamion);
        camion.setCoordX(nuevaX);
        camion.setCoordY(nuevaY);
    }

    public void reasignarPeticion(int idCamionOrigen, int idCamionDestino, int idPeticion) {
        Camion camionOrigen = estado_actual.getCamiones().get(idCamionOrigen);
        Camion camionDestino = estado_actual.getCamiones().get(idCamionDestino);

        

        Peticion peticion = camionOrigen.removePeticion(idPeticion);
        camionDestino.addPeticion(peticion);
    }

    public void intercambiaPeticiones(int idCamionA, int idCamionB, int idPeticionA, int idPeticionB) {
        Camion camionA = estado_actual.getCamiones().get(idCamionA);
        Camion camionB = estado_actual.getCamiones().get(idCamionB);

        Peticion temp = camionA.popPeticion(idPeticionA);
        Peticion temp2 = camionB.popPeticion(idPeticionB);
        camionA.addPeticion(temp2);
        camionB.addPeticion(temp);
    }



    /* Heuristic function */

    public double heuristic(){ return 0; }

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

    private double distancia(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

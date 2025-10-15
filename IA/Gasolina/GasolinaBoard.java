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

    /* Heuristic function */

    public double heuristic(){ return 0; }

    /* Goal test */

     public boolean is_goal(){ return true; } // --------------- no se si hace falta

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */

    private double distancia(int x1, int y1, int x2, int y2) {

        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

package IA.Gasolina;

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

    private Estado estado;
    private int [] board;
    private static int [] solution;

    /* Constructor */
    public GasolinaBoard(Estado estado_inicial) {
        this.estado = estado_inicial;
        // Initialize board and solution
        // ^^^^^ TO COMPLETE ^^^^^

    }

    public void flip_it(int i){
        // ^^^^^ TO COMPLETE ^^^^^
    }

    /* Heuristic function */
    public double heuristic(){
        // compute the number of coins out of place respect to solution
        return 0;
    }

     /* Goal test */
     public boolean is_goal(){
         // compute if board = solution
         return true;
     }

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */

    public int[] getBoard() {
        return board;
    }

    public int[] getSolution() {
        return solution;
    }
    public Estado getEstado() {
        return estado;
    }

}

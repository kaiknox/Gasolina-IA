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

    private int [] board;
    private static int [] solution;

    /* Constructor */
    public GasolinaBoard(int []init, int[] goal) {

        board = new int[init.length];
        solution = new int[init.length];

        for (int i = 0; i< init.length; i++) {
            board[i] = init[i];
            solution[i] = goal[i];
        }

    }

    /* vvvvv TO COMPLETE vvvvv */
    public void flip_it(int i){
        // flip the coins i and i + 1
        if(i == 4){ board[0] = (board[0]==0)? 1:0; board[i] = (board[i]==0)? 1:0;}
        else {
            board[i] = (board[i]==0)? 1:0;
            board[i + 1] = (board[i + 1]==0)? 1:0;
        }
    }

    /* Heuristic function */
    public double heuristic(){
        // compute the number of coins out of place respect to solution
        int count = 0;
        for(int i = 0; i < board.length; i++)
            if(board[i] != solution[i]) count++;
        return count;
    }

     /* Goal test */
     public boolean is_goal(){
         // compute if board = solution
         for(int i = 0; i < board.length; i++){
             if(board[i] != solution[i]) return false;
        }
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

}

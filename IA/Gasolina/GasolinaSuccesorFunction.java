package IA.Gasolina;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class GasolinaSuccesorFunction implements SuccessorFunction{

    public List<Successor> getSuccessors(Object state){
        ArrayList<Successor> retval = new ArrayList<>();
        GasolinaBoard board = (GasolinaBoard) state;

        for(int i = 0; i < 5; i++){
            GasolinaBoard new_board = new GasolinaBoard(board.getBoard(), board.getSolution());
            new_board.flip_it(i);
            Successor s = new Successor("flip " + i, new_board);
            retval.add(s);
        }

        return retval;

    }

}

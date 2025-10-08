package IA.Gasolina;

import aima.search.framework.HeuristicFunction;

public class GasolinaHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){

        return ((GasolinaBoard) n).heuristic();
    }
}

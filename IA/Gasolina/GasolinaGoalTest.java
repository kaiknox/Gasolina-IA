package IA.Gasolina;

import aima.search.framework.GoalTest;

/**
 * Goal test moved/renamed from ProbIA5GoalTest
 */
public class GasolinaGoalTest implements GoalTest {

    public boolean isGoalState(Object state){

        return((GasolinaBoard) state).is_goal();
    }
}

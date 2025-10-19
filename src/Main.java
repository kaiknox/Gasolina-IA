import IA.Gasolina.GasolinaBoard;
import IA.Gasolina.GasolinaGoalTest;
import IA.Gasolina.GasolinaHeuristicFunction;
import IA.Gasolina.GasolinaSuccesorFunction;
import IA.Gasolina.Estado;
import IA.Gasolina.Viaje;
import IA.Gasolina.Viajes;
import IA.Gasolina.Gasolineras;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import IA.Gasolina.Gasolinera;
import IA.Gasolina.Distribucion;
import IA.Gasolina.Gasolineras;
import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Camion;

import java.util.List;
import java.util.ArrayList;


public class Main {

    public static List<Gasolinera> gasolineras;
    public static List<Distribucion> centros;

    public static int numGasolineras = 100;
    public static int numCentros = 10;
    public static int seed = 1234;

    public static void main(String[] args) throws Exception{

        // Inicializar problema:
        
        Main.gasolineras = new Gasolineras(numGasolineras, seed);
        Main.centros = new CentrosDistribucion(numCentros, 1, seed);
        
        List<Camion> camiones = new ArrayList<>();
        for(int i = 0; i < numCentros; ++i) {
            camiones.add(new Camion(Main.centros.get(i).getCoordX(), Main.centros.get(i).getCoordY()));
        }

        Estado estado_inicial = new Estado(camiones);

        GasolinaBoard board = new GasolinaBoard(estado_inicial, Main.gasolineras, Main.centros);
        
        // CAMBIO: usar 2 (round-robin) para tener un estado inicial peor y que HC pueda mejorar
        board.crearEstadoInicial(2); // 1 = asignar al más cercano, 2 = round-robin

        //double costeInicial = -board.heuristic();
        //board.escribirEstadoActual();

        GasolinaBoard estadoInicial = board; // tu estado inicial
        GasolinaHeuristicFunction heuristica = new GasolinaHeuristicFunction();

        double heuristicaInicial = heuristica.getHeuristicValue(estadoInicial);
        double beneficioInicial = estadoInicial.calcularBeneficio();

        // Create the Problem object
        Problem p = new  Problem(board,
                                new GasolinaSuccesorFunction(),
                                new GasolinaGoalTest(),
                                new GasolinaHeuristicFunction());

        // Instantiate the search algorithm
	// AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search search = new HillClimbingSearch();

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, search);

	// We print the results of the search
        System.out.println();
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        // You can access also to the goal state using the
	// method getGoalState of class Search

    double costeFinal = -board.heuristic();
    //board.escribirEstadoActual();

    // System.out.println("Coste inicial: " + costeInicial);
    // System.out.println("Coste final: " + costeFinal);

    GasolinaBoard estadoFinal = (GasolinaBoard)search.getGoalState(); // el estado final encontrado

    double heuristicaFinal = heuristica.getHeuristicValue(estadoFinal);
    double beneficioFinal = estadoFinal.calcularBeneficio();

    System.out.println("Heurística inicial: " + heuristicaInicial);
    System.out.println("Heurística final: " + heuristicaFinal);
    System.out.println("Beneficio inicial: " + beneficioInicial);
    System.out.println("Beneficio final: " + beneficioFinal);

    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
        
    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}
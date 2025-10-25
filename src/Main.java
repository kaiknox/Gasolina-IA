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
import java.util.Scanner;


public class Main {

    public static List<Gasolinera> gasolineras;
    public static List<Distribucion> centros;

    public static int numGasolineras = 100;
    public static int numCentros = 10;
    public static int seed = 1234;

    // Parámetros de búsqueda
    public static String algoritmo = "SA"; // "HC" = Hill Climbing, "SA" = Simulated Annealing
    
    // Parámetros para Simulated Annealing
    public static int stepsSA = 1000;     // Número de iteraciones
    public static int stirsSA = 100;      // Cambios de temperatura
    public static int kSA = 20;            // Parámetro k para la función de temperatura
    public static double lambdaSA = 0.001; // Parámetro lambda para la función de temperatura

    public static void main(String[] args) throws Exception{
        
        // Permitir elegir algoritmo por argumento de línea de comandos
        if (args.length > 0) {
            algoritmo = args[0].toUpperCase();
        } else {
            // Si no hay argumentos, preguntar al usuario
            Scanner scanner = new Scanner(System.in);
            System.out.println("===========================================");
            System.out.println("Seleccione el algoritmo de búsqueda:");
            System.out.println("1. Hill Climbing (HC)");
            System.out.println("2. Simulated Annealing (SA)");
            System.out.println("===========================================");
            System.out.print("Ingrese su opción (1 o 2): ");
            
            int opcion = scanner.nextInt();
            if (opcion == 1) {
                algoritmo = "HC";
            } else if (opcion == 2) {
                algoritmo = "SA";
            } else {
                System.out.println("Opción inválida. Usando Hill Climbing por defecto.");
                algoritmo = "HC";
            }
        }
        System.out.println();

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
        board.crearEstadoInicial(1); // 1 = asignar al más cercano, 2 = round-robin

        //double costeInicial = -board.heuristic();
        //board.escribirEstadoActual();

        GasolinaBoard estadoInicial = board; // tu estado inicial
        //estadoInicial.escribirEstadoActual();
        GasolinaHeuristicFunction heuristica = new GasolinaHeuristicFunction();

        double heuristicaInicial = heuristica.getHeuristicValue(estadoInicial);
        double beneficioInicial = estadoInicial.calcularBeneficio();

        long start = System.currentTimeMillis();

        // Create the Problem object
        Problem p = new  Problem(board,
                                new GasolinaSuccesorFunction(),
                                new GasolinaGoalTest(),
                                new GasolinaHeuristicFunction());

        // Instantiate the search algorithm based on user selection
        Search search;
        if (algoritmo.equals("SA")) {
            System.out.println("=== Usando Simulated Annealing ===");
            System.out.println("Parámetros: steps=" + stepsSA + ", stirs=" + stirsSA + ", k=" + kSA + ", lambda=" + lambdaSA);
            search = new SimulatedAnnealingSearch(stepsSA, stirsSA, kSA, lambdaSA);
        } else {
            System.out.println("=== Usando Hill Climbing ===");
            search = new HillClimbingSearch();
        }

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
    long end = System.currentTimeMillis();

    GasolinaBoard estadoFinal = (GasolinaBoard)search.getGoalState(); // el estado final encontrado

    double heuristicaFinal = heuristica.getHeuristicValue(estadoFinal);
    double beneficioFinal = estadoFinal.calcularBeneficio();
    //estadoFinal.escribirEstadoActual();
    System.out.println("Heurística inicial: " + heuristicaInicial);
    System.out.println("Heurística final: " + heuristicaFinal);
    System.out.println("Beneficio inicial: " + beneficioInicial);
    System.out.println("Beneficio final: " + beneficioFinal);

    System.out.println("Tiempo de ejecución: " + (end - start) + " ms");

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
            Object action = actions.get(i);
            // Simulated Annealing puede devolver estados en lugar de strings
            if (action instanceof String) {
                System.out.println(action);
            } else {
                System.out.println("Action " + i + ": " + action.getClass().getSimpleName());
            }
        }
    }
}
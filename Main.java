import IA.Gasolina.GasolinaBoard;
import IA.Gasolina.GasolinaGoalTest;
import IA.Gasolina.GasolinaHeuristicFunction;
import IA.Gasolina.GasolinaSuccesorFunction;
import IA.Gasolina.Estado;
import IA.Gasolina.Viaje;
import IA.Gasolina.Viajes;
import IA.Gasolina.Gasolineras;
import IA.Gasolina.CentrosDistribucion;
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
import IA.Gasolina.Camion;
import java.util.ArrayList;


public class Main {

    public static List<Gasolinera> gasolineras;
    public static List<Distribucion> centros;
    public static Estado estado_inicial;

    public static int numGasolineras = 10;
    public static int numCentros = 10;
    public static int seed = 12345;

    public static void escribirEstado() {

        System.out.println("---------- Coordenadas de las Gasolineras ----------");

        for(int i = 0; i < gasolineras.size(); ++i) {
            System.out.println("Gasolinera " + i + ": " + gasolineras.get(i).getCoordX() + ", " + gasolineras.get(i).getCoordY());
        }

        System.out.println("---------- Coordenadas de los Centros ----------");

        for(int i = 0; i < centros.size(); ++i) {
            System.out.println("Centros " + i + ": " + centros.get(i).getCoordX() + ", " + centros.get(i).getCoordY());
        }

        System.out.println("---------- Coordenadas de los Camiones ----------");
        List<Camion> camiones = estado_inicial.getCamiones();
        for(int i = 0; i < camiones.size(); ++i) {
            System.out.println("Camiones " + i + ": " + camiones.get(i).getCoordX() + ", " + camiones.get(i).getCoordY());
            List<Viajes> viajes = camiones.get(i).getListaViajes();
            for(int j = 0; j < viajes.size(); ++j) {
                System.out.println("Viaje " + j + ": distancia total = " + viajes.get(j).getDistanciaTotal() + ", tiempo total = " + viajes.get(j).getTiempoTotal() + ", cantidad = " + viajes.get(j).getCantidad());
                List<Viaje> listaViajes = viajes.get(j).getListaViajes();
                for(int k = 0; k < listaViajes.size(); ++k) {
                    System.out.println("    Peticion " + (k+1) + ": Inicio= " + listaViajes.get(k).getCoordX_inicio() + "," + listaViajes.get(k).getCoordY_inicio() + ", Fin= " + listaViajes.get(k).getCoordX_fin() + "," + listaViajes.get(k).getCoordY_fin() + ", cantidad = " + listaViajes.get(k).getCantidad() + ", distancia = " + listaViajes.get(k).getDistanciaTotal() + ", tiempo = " + listaViajes.get(k).getTiempoTotal());
                }
            }
        }

    }

    public static void main(String[] args) throws Exception{

        // Inicializar problema:
        
        gasolineras = new Gasolineras(numGasolineras, seed);
        centros = new CentrosDistribucion(numCentros, 1, seed);
        List<Camion> camiones = new ArrayList<>();
        for(int i = 0; i < numCentros; ++i) {
            camiones.add(new Camion(centros.get(i).getCoordX(), centros.get(i).getCoordY()));
        }

        estado_inicial = new Estado(camiones);// 0, 1 o 2 dependiendo de la función que queramos usar, 0 es ninguna función.

    GasolinaBoard board = new GasolinaBoard(estado_inicial, gasolineras, centros);
        board.crearEstadoInicial(1);
        escribirEstado();
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

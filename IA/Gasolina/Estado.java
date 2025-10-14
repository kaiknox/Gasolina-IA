package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Estado {

    private Gasolineras gasolineras;
    private CentrosDistribucion centros;
    private List<Camion> camiones;

    public void crearEstadoInicial(Gasolineras gasolinerasParam, CentrosDistribucion centrosParam) {
        // Store provided collections
        this.gasolineras = gasolinerasParam;
        this.centros = centrosParam;

        // Initialize camiones list
        this.camiones = new ArrayList<>();

        // Create a Camion for each distribution center
        for (int i = 0; i < centros.size(); i++) {
            Distribucion centro = centros.get(i);
            camiones.add(new Camion(centro.getCoordX(), centro.getCoordY()));
        }

        ///////// Mas adelante hay que crear un estado inicial con una solución mejor.
    }

    public Gasolineras getGasolineras() { return gasolineras; }
    public CentrosDistribucion getCentros() { return centros; }
    public List<Camion> getCamiones() { return camiones; }

}

package IA.Gasolina;

import java.util.List;
import java.util.ArrayList;

public class Estado {
    
    public List<Camion> camiones;

    public Estado(List<Camion> camiones) {
        this.camiones = new ArrayList<>(camiones);
        this.gasolineras = null;
        this.centrosDistribucion = null;
    }

    /**
     * New constructor: accept references to gasolineras and centros so initializers can use them
     */
    public Estado(List<Camion> camiones, List<Gasolinera> gasolineras, List<Distribucion> centros) {
        this.camiones = new ArrayList<>(camiones);
        this.gasolineras = gasolineras;
        this.centrosDistribucion = centros;
    }

    public List<Camion> getCamiones() {
        return camiones;
    }
}

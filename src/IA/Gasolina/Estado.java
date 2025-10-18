package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Estado {
    
    public List<Camion> camiones;

    public Estado() {
        this.camiones = new ArrayList<>();
    }

    /**
     * New constructor: accept references to gasolineras and centros so initializers can use them
     */
    public Estado(List<Camion> camiones) {
        this.camiones = new ArrayList<>(camiones);
    }

    public List<Camion> getCamiones() {
        return camiones;
    }
    public void setCamiones(List<Camion> camiones) {
        this.camiones = camiones;
    }
}

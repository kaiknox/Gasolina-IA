package IA.Gasolina;

import java.util.List;
import java.util.ArrayList;

public class Estado {
    
    public List<Camion> camiones;

    public Estado(List<Camion> camiones) {
        this.camiones = new ArrayList<>(camiones);
    }

    public void crearEstadoInicial(int funcionAescoger) {
        if(funcionAescoger == 1) {
            crearEstadoInicial1();
        } 
        else if (funcionAescoger == 2) {
            crearEstadoInicial2();
        }
    }

    private void crearEstadoInicial1() {
        ///////// Mas adelante hay que crear un estado inicial con una solución mejor.
    }

    private void crearEstadoInicial2() {
        ///////// Mas adelante hay que crear un estado inicial con una solución mejor.
    }

    public List<Camion> getCamiones() {
        return camiones;
    }
}

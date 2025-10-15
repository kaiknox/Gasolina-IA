package IA.Gasolina;

import java.util.List;
import java.util.ArrayList;

public class Camion {

    private int coordX;
    private int coordY;
    private int deposito;  // 2, 1 o 0, dependiendo de la cantidad de depositos llenos que le queden.
    private List<Viaje> viajes; // Lista de viajes asignados al camion

    public Camion(int x, int y){
        this.coordX = x;
        this.coordY = y;
        this.deposito = 2;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public int getDeposito() {
        return deposito;
    }

    public void setDeposito(int deposito) {
        this.deposito = deposito;
    }

}

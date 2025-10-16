package IA.Gasolina;

import java.util.ArrayList;
import java.util.List;

public class Camion {

    private int coordX;
    private int coordY;
    private int deposito;  // 2, 1 o 0, dependiendo de la cantidad de depositos llenos que le queden.
    private List<Viajes> viajesCamion; // Lista de viajes asignados al camion
    private double DistanciaRecorrida; // Distancia total recorrida por el camion
    public static int maxViajes = 5;
    public static int DistanciaMaxima = 640;
    public static int HorasJornada = 8;
    public static double HorasTrabajadas = 0.0;
    public static int VelocidadMedia = 80; // km/h

    public Camion(int x, int y) {
        this.coordX = x;
        this.coordY = y;
        this.deposito = 2;
        viajesCamion = new ArrayList<>();
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

    public List<Viajes> getViajes() {
        return viajesCamion;
    }

    public void setViajes(List<Viajes> viajes) {
        this.viajesCamion = viajes;
    }
    public void addViaje(Viajes viajes, int maxViajes){
        // Asumimos que el viaje ya ha sido validado antes de añadirlo
        // Añadir un viaje a la lista de viajes del camion
        this.viajesCamion.add(viajes);
        this.DistanciaRecorrida += viajes.getDistanciaTotal();
        this.HorasTrabajadas += viajes.getTiempoTotal();
    }

    public double getTiempoDistancia(Viajes viajes) {
        return viajes.getTiempoTotal();
    }

    public boolean puedeAñadirViaje() {
        return (this.viajesCamion.size() < maxViajes && this.DistanciaRecorrida < DistanciaMaxima && HorasTrabajadas < HorasJornada);
    }

    public double getDistanciaRecorrida() {
        return DistanciaRecorrida;
    }
    public void setDistanciaRecorrida(double distanciaRecorrida) {
        DistanciaRecorrida = distanciaRecorrida;
    }
    public double getHorasTrabajadas() {
        return HorasTrabajadas;
    }
    public void setHorasTrabajadas(double horasTrabajadas) {
        HorasTrabajadas = horasTrabajadas;
    }

    public double getBeneficio(){
        double beneficioTotal = 0;
        for(int i=0; i < viajesCamion.size(); i++){
            beneficioTotal += viajesCamion.get(i).getCantidad();
        }
        return beneficioTotal;
    }
}

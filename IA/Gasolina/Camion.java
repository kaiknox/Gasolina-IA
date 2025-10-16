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
    private double horasTrabajadas = 0.0;
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
    public void addViaje(Viajes viajes){
        // Asumimos que el viaje ya ha sido validado antes de añadirlo
        // Añadir un viaje a la lista de viajes del camion
    this.viajesCamion.add(viajes);
    this.DistanciaRecorrida += viajes.getDistanciaTotal();
    this.horasTrabajadas += viajes.getTiempoTotal();
    }

    public double getTiempoDistancia(Viajes viajes) {
        return viajes.getTiempoTotal();
    }

    public boolean puedeAñadirViaje() {
        return (this.viajesCamion.size() < maxViajes && this.DistanciaRecorrida < DistanciaMaxima && this.horasTrabajadas < HorasJornada);
    }

    public double getDistanciaRecorrida() {
        return DistanciaRecorrida;
    }
    public void setDistanciaRecorrida(double distanciaRecorrida) {
        DistanciaRecorrida = distanciaRecorrida;
    }
    public double getHorasTrabajadas() {
        return horasTrabajadas;
    }
    public void setHorasTrabajadas(double horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public double getBeneficio(){
        double beneficioTotal = 0;
        for(int i=0; i < viajesCamion.size(); i++){
            beneficioTotal += viajesCamion.get(i).getCantidad();
        }
        return beneficioTotal;
    }

    public void addPeticion(Gasolinera gasolinera, int diasPendientes){
        // Create the new Viaje for this petition
        Viaje viaje = new Viaje(this.coordX, this.coordY, gasolinera.getCoordX(), gasolinera.getCoordY());

        System.out.println("[DEBUG] addPeticion: camion at ("+coordX+","+coordY+") trying to add viaje to "+gasolinera+" (dist="+viaje.getDistanciaTotal()+", tiempo="+viaje.getTiempoTotal()+")");
        System.out.println("[DEBUG] current viajes count="+this.viajesCamion.size()+", distanciaRec="+this.DistanciaRecorrida+", horasTrab="+this.horasTrabajadas);

        // First try to add the viaje into an existing Viajes that has space
        for (Viajes v : this.viajesCamion) {
            if (v == null) continue;
            System.out.println("[DEBUG] checking existing Viajes (size="+v.getListaViajes().size()+") puedeAñadir="+v.puedeAñadir());
            if (v.puedeAñadir()) {
                double nuevaDist = this.DistanciaRecorrida + viaje.getDistanciaTotal();
                double nuevasHoras = this.horasTrabajadas + viaje.getTiempoTotal();
                System.out.println("[DEBUG] simulated totals: dist="+nuevaDist+" vs max="+DistanciaMaxima+", horas="+nuevasHoras+" vs max="+HorasJornada);
                // check camion-level constraints (distance and hours)
                if (nuevaDist <= DistanciaMaxima && nuevasHoras <= HorasJornada) {
                    v.añadirViaje(viaje);
                    // update camion totals
                    this.DistanciaRecorrida = nuevaDist;
                    this.horasTrabajadas = nuevasHoras;
                    System.out.println("[DEBUG] added to existing Viajes. new viajes count="+v.getListaViajes().size());
                    return; // added successfully
                } else {
                    System.out.println("[DEBUG] cannot add to this Viajes: constraints violated");
                }
            }
        }

        // No existing Viajes had space or constraints would be violated. Try to create a new Viajes if camion allows another trip group.
        if (!this.puedeAñadirViaje()) {
            // cannot add another Viajes due to camion-level constraints: prune and do nothing
            System.out.println("[DEBUG] cannot create new Viajes: camion cannot accept more trips or limits reached");
            return;
        }

        Viajes nuevo = new Viajes();
        // check totals before adding
        if (this.DistanciaRecorrida + viaje.getDistanciaTotal() <= DistanciaMaxima && this.horasTrabajadas + viaje.getTiempoTotal() <= HorasJornada) {
            System.out.println("[DEBUG] creating new Viajes and adding viaje");
            nuevo.añadirViaje(viaje);
            addViaje(nuevo); // this updates DistanciaRecorrida and HorasTrabajadas
            System.out.println("[DEBUG] added new Viajes. camion viajes count="+this.viajesCamion.size()+", distanciaRec="+this.DistanciaRecorrida+", horasTrab="+this.horasTrabajadas);
        } else {
            // cannot add due to totals; prune
            System.out.println("[DEBUG] cannot create new Viajes: totals would exceed limits");
            return;
        }
    }

    public List<Viajes> getListaViajes() {
        return viajesCamion;
    }
}

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
    public static double DistanciaMaxima = 640.0;
    public static double HorasJornada = 8.0;
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
        if(this.viajesCamion.size() >= maxViajes || ((this.DistanciaRecorrida + viajes.getDistanciaTotal()) >= DistanciaMaxima) || ((this.horasTrabajadas + viajes.getTiempoTotal()) >= HorasJornada)){
            throw new IllegalStateException("No se pueden añadir más viajes al camión: límite alcanzado.");
        }
        this.viajesCamion.add(viajes);
        this.DistanciaRecorrida += viajes.getDistanciaTotal();
        this.horasTrabajadas += viajes.getTiempoTotal();
    }

    public double getTiempoDistancia(Viajes viajes) {
        return viajes.getTiempoTotal();
    }

    public boolean puedeAñadirViaje() {
        return (this.viajesCamion.size() < maxViajes && this.DistanciaRecorrida < DistanciaMaxima && this.horasTrabajadas < HorasJornada );
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

    /*  public double getBeneficio(){
        double beneficioTotal = 0;
        for(int i=0; i < viajesCamion.size(); i++){
            beneficioTotal += viajesCamion.get(i).getCantidad();
        }
        return beneficioTotal;
    } */    //funcion anterior para comparar

    public double getBeneficio(){
        double beneficioTotal = 0.0;
        final double PRECIO_BASE = 1000.0; // precio base por depósito (ajusta si tienes otro valor)
        
        for (Viajes viajesGrupo : viajesCamion) {
            if (viajesGrupo == null) continue;
            
            // Cada viaje dentro del grupo de viajes tiene sus días pendientes
            for (Viaje viaje : viajesGrupo.getListaViajes()) {

                if(viaje.isProvisionalReturn()) continue; // ignorar tramos de retorno provisional
                int diasPendientes = viaje.getDiasPendientes(); // asume que Viaje tiene este getter
                
                double porcentajePrecio;
                if (diasPendientes == 0) {
                    porcentajePrecio = 102.0; // 102%
                } else {
                    porcentajePrecio = 100.0 - (Math.pow(2.0 , diasPendientes)); // (100 - 2^días)%
                    // Protección: si los días son muchos, el porcentaje puede ser negativo
                    if (porcentajePrecio < 0) porcentajePrecio = 0;
                }
                
                // Beneficio = precio_base × (porcentaje / 100)
                beneficioTotal += PRECIO_BASE * (porcentajePrecio / 100.0);
            }
        }
        
        return beneficioTotal;
    }

    public double getPerdida() {
        double perdidaTotal = 0.0;
        for(Viajes viajesGrupo : viajesCamion) {
            if (viajesGrupo == null) continue;
            for (Viaje viaje : viajesGrupo.getListaViajes()) {
                int diasPendientes = viaje.getDiasPendientes();
                if (diasPendientes > 0) {
                    double porcentajePerdida = 100.0-(Math.pow(2.0, diasPendientes)); // 2×días%
                    double porcentajePerdida1daymore = 100.0-(Math.pow(2.0, diasPendientes+1)); // 2×días+1%
                    perdidaTotal += (1000.0 * (porcentajePerdida/100.0)) - (1000.0 * (porcentajePerdida1daymore/100.0)); // usando el mismo precio base
                }
            }
        }
        return perdidaTotal;
    }

    public void addPeticion(Gasolinera gasolinera, int diasPendientes){
        // Create the new Viaje for this petition
        Viaje viaje = new Viaje(this.coordX, this.coordY, gasolinera.getCoordX(), gasolinera.getCoordY(), diasPendientes);

        //System.out.println("[DEBUG] addPeticion: camion at ("+coordX+","+coordY+") trying to add viaje to "+gasolinera+" (dist="+viaje.getDistanciaTotal()+", tiempo="+viaje.getTiempoTotal()+")");
        //System.out.println("[DEBUG] current viajes count="+this.viajesCamion.size()+", distanciaRec="+this.DistanciaRecorrida+", horasTrab="+this.horasTrabajadas);

        // Build the leg according to current state of existing Viajes in the camion.
        // If there is an open Viajes with size 0 -> need primer tramo (centro->gasolinera)
        // If size 1 -> need segundo tramo (gasolinera->gasolinera)
        // If size 2 -> need tercer tramo (gasolinera->centro)

        for (Viajes v : this.viajesCamion) {
            if (v == null) continue;
            int currentSize = v.getListaViajes().size();
            //System.out.println("[DEBUG] checking existing Viajes (size=" + currentSize + ") puedeAñadir=" + v.puedeAñadir());
            if (!v.puedeAñadir()) continue;

            Viaje leg;
            try {
                if (currentSize == 2) {
                    // tercer tramo: salir de última gasolinera y volver al centro
                    Viaje prev = v.getListaViajes().get(1);
                    leg = new Viaje(prev.getCoordX_inicio(), prev.getCoordY_inicio(), gasolinera.getCoordX(), gasolinera.getCoordY(), diasPendientes);
                } else {
                    // should not be here (puedeAñadir would be false)
                    continue;
                }
            } catch (Exception ex) {
                //System.out.println("[DEBUG] error building leg: " + ex.getMessage());
                continue;
            }

            double nuevaDist = this.DistanciaRecorrida + leg.getDistanciaTotal();
            double nuevasHoras = this.horasTrabajadas + leg.getTiempoTotal();
            //System.out.println("dist actual=" + this.DistanciaRecorrida + " dist=" + nuevaDist + " vs max=" + DistanciaMaxima + ", horas actuales=" + this.horasTrabajadas + " horas=" + nuevasHoras + " vs max=" + HorasJornada);
            if ((nuevaDist <= DistanciaMaxima) && (nuevasHoras <= HorasJornada)) {
                //System.out.println("[DEBUG] can add to this Viajes: adding leg of type size=" + currentSize);
                try {
                    v.añadirViaje(leg, this);
                    // update camion totals
                    this.DistanciaRecorrida = nuevaDist;
                    this.horasTrabajadas = nuevasHoras;
                    //System.out.println("[DEBUG] added leg to existing Viajes. new viajes size=" + v.getListaViajes().size());
                    return; // added successfully
                } catch (IllegalArgumentException | IllegalStateException e) {
                    //System.out.println("[DEBUG] cannot add to this Viajes: " + e.getMessage());
                }
            } else {
                //System.out.println("[DEBUG] cannot add to this Viajes: constraints violated");
            }
        }

        // No existing Viajes had space or constraints would be violated. Try to create a new Viajes if camion allows another trip group.
        if (!this.puedeAñadirViaje()) {
            // cannot add another Viajes due to camion-level constraints: prune and do nothing
            //System.out.println("[DEBUG] cannot create new Viajes: camion cannot accept more trips or limits reached");
            return;
        }

        Viajes nuevo = new Viajes();
        // For a new Viajes, the first leg must be centro->gasolinera
        Viaje firstLeg = new Viaje(this.coordX, this.coordY, gasolinera.getCoordX(), gasolinera.getCoordY(), diasPendientes);
        //Viaje vuelta = new Viaje(gasolinera.getCoordX(), gasolinera.getCoordY(), this.coordX, this.coordY, 0);
        if ((this.DistanciaRecorrida + firstLeg.getDistanciaTotal()*2) <= DistanciaMaxima && (this.horasTrabajadas + firstLeg.getTiempoTotal()*2) <= HorasJornada) {
            //System.out.println("[DEBUG] creating new Viajes and adding first leg");
            nuevo.añadirViaje(firstLeg, this);
            //nuevo.añadirViaje(vuelta, this);
            addViaje(nuevo); // this updates DistanciaRecorrida and HorasTrabajadas
            //System.out.println("[DEBUG] added new Viajes. camion viajes count=" + this.viajesCamion.size() + ", distanciaRec=" + this.DistanciaRecorrida + ", horasTrab=" + this.horasTrabajadas);
            return;
        } else {
            // cannot add due to totals; prune
            //System.out.println("[DEBUG] cannot create new Viajes: totals would exceed limits");
            return;
        }
    }

    public void fixViajes(){
        for(Viajes v : viajesCamion){
            v.fixProvisionalReturn();
        }
    }

    public List<Viajes> getListaViajes() {
        return viajesCamion;
    }

    public int contarPeticionesAsignadas() {
        int total = 0;
        for (Viajes vg : viajesCamion) {
            for (Viaje t : vg.getListaViajes())
                if(!t.isProvisionalReturn()){
                    total++;
                }
        }
        return total;
    }

}

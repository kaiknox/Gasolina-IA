package IA.Gasolina;


import java.util.List;
/**
 * Board implementation moved/renamed from ProbIA5Board
 */
public class GasolinaBoard {
    /* Class independent from AIMA classes
       - It has to implement the state of the problem and its operators
     *

    /* State data structure
        vector with the parity of the coins (we can assume 0 = heads, 1 = tails
     */

    private Estado estado_actual;

    /* Constructor */
    public GasolinaBoard(Estado estado_inicial) {

        this.estado_actual = estado_inicial;
    }

    /* OPERADORES */
    public void moverCamion(int idCamion, int nuevaX, int nuevaY) {
        Camion camion = estado_actual.getCamiones().get(idCamion);
        camion.setCoordX(nuevaX);
        camion.setCoordY(nuevaY);
    }

    // Reasigna un viaje de un camion a otro
    public void reasignarViajes(int idCamionOrigen, int idCamionDestino, int idViaje) {
        Camion camionOrigen = estado_actual.getCamiones().get(idCamionOrigen);
        Camion camionDestino = estado_actual.getCamiones().get(idCamionDestino);

        // Hay que buscar la peticion en el camion origen y eliminarla
        // Luego añadirla al camion destino
        List<Viajes> viajesOrigen = camionOrigen.getViajes();
        List<Viajes> viajesDestino = camionDestino.getViajes();

        Viajes viajeARemover = viajesOrigen.get(idViaje);

        if (viajeARemover != null) {
            viajesOrigen.remove(viajeARemover);
            camionOrigen.setDistanciaRecorrida(camionOrigen.getDistanciaRecorrida() - viajeARemover.getDistanciaTotal());
            camionOrigen.setHorasTrabajadas(camionOrigen.getHorasTrabajadas() - viajeARemover.getTiempoTotal());
            viajesDestino.add(viajeARemover);
        }
    }

    // Intercambia dos viajes entre dos camiones
    public void intercambiaViajes(int idCamionA, int idCamionB, int idViajeA, int idViajeB) {
        Camion camionA = estado_actual.getCamiones().get(idCamionA);
        Camion camionB = estado_actual.getCamiones().get(idCamionB);

        List<Viajes> viajesOrigen = camionA.getViajes();
        List<Viajes> viajesDestino = camionB.getViajes();

        Viajes viajeA = viajesOrigen.get(idViajeA);
        Viajes viajeB = viajesDestino.get(idViajeB);

        if (viajeA != null && viajeB != null ) {
            viajesOrigen.remove(viajeA);
            camionA.setDistanciaRecorrida(camionA.getDistanciaRecorrida() - viajeA.getDistanciaTotal() + viajeB.getDistanciaTotal());
            camionA.setHorasTrabajadas(camionA.getHorasTrabajadas() - viajeA.getTiempoTotal() + viajeB.getTiempoTotal());
            viajesDestino.remove(viajeB);
            camionB.setDistanciaRecorrida(camionB.getDistanciaRecorrida() - viajeB.getDistanciaTotal() + viajeA.getDistanciaTotal());
            camionB.setHorasTrabajadas(camionB.getHorasTrabajadas() - viajeB.getTiempoTotal() + viajeA.getTiempoTotal());
            viajesOrigen.add(viajeB);
            viajesDestino.add(viajeA);
        }

        
    }

    /* Getters and setters */
    public Estado getEstado_actual() {
        return estado_actual;
    }

    /* Heuristic function */

    public double heuristic(){
        List<Camion> camiones = estado_actual.getCamiones();
        double beneficioTotal = 0.0;
        for (int i=0; i<camiones.size(); i++){
            beneficioTotal += camiones.get(i).getBeneficio();
        }
        return beneficioTotal;
    }

    /* Goal test */

     public boolean is_goal(){ return true; } // --------------- no se si hace falta

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */
}

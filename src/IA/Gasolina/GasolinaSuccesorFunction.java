package IA.Gasolina;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class GasolinaSuccesorFunction implements SuccessorFunction{

    public List<Successor> getSuccessors(Object state){
        ArrayList<Successor> retval = new ArrayList<>();
        GasolinaBoard board = (GasolinaBoard) state;

        Estado estado = board.getEstado_actual();
        List<Camion> camiones = estado.getCamiones();
        int n = camiones.size();

        // REASIGNAR: move a Viajes from camion i to camion j (single-step successors)
        for (int i = 0; i < n; i++) {
            Camion origen = camiones.get(i);
            List<Viajes> viajesOrigen = origen.getViajes();
            if (viajesOrigen == null) continue;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Camion destino = camiones.get(j);
                List<Viajes> viajesDestino = destino.getViajes();
                if (viajesDestino == null) continue;

                for (int k = 0; k < viajesOrigen.size(); k++) {
                    // Prune: only consider adding the viaje if the destination truck can accept a trip
                    if (!destino.puedeAñadirViaje()) continue;

                    Estado estadoCopy = deepCopyEstado(estado);
                    GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                    newBoard.reasignarViajes(i, j, k);
                    Successor s = new Successor("Reasignar viaje " + k + " de camión " + i + " a camión " + j, newBoard);
                    retval.add(s);
                }
            }
        }

        // INTERCAMBIAR: swap viajes between two trucks (single-step successors)
         
        for (int a = 0; a < n; a++) {
            Camion ca = camiones.get(a);
            List<Viajes> va = ca.getViajes();
            if (va == null) continue;
            for (int b = a + 1; b < n; b++) {
                Camion cb = camiones.get(b);
                List<Viajes> vb = cb.getViajes();
                if (vb == null) continue;

                for (int ia = 0; ia < va.size(); ia++) {
                    for (int ib = 0; ib < vb.size(); ib++) {
                        Estado estadoCopy = deepCopyEstado(estado);
                        GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                        newBoard.intercambiaViajes(a, b, ia, ib);
                        Successor s = new Successor("Intercambiar viaje " + ia + " de camión " + a + " con viaje " + ib + " de camión " + b, newBoard);
                        retval.add(s);
                    }
                }
            }
        }
        

        // INVERTIR ORDEN, DIVIDIR y SWAP: para cada viaje con dos gasolineras
        for (int i = 0; i < n; i++) {
            Camion camion = camiones.get(i);
            List<Viajes> viajes = camion.getViajes();
            if (viajes == null) continue;
            for (int k = 0; k < viajes.size(); k++) {
                Viajes v = viajes.get(k);
                // Solo si el viaje tiene exactamente dos gasolineras (tramos no provisionales)
                int countGas = 0;
                for (Viaje tramo : v.getListaViajes()) {
                    if (!tramo.isProvisionalReturn()) countGas++;
                }
                if (countGas == 2) {
                    Estado estadoCopy = deepCopyEstado(estado);
                    GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                    newBoard.invertirOrdenViaje(i, k);
                    Successor s = new Successor("Invertir orden viaje " + k + " de camión " + i, newBoard);
                    retval.add(s);

                    // Dividir viaje en dos
                    estadoCopy = deepCopyEstado(estado);
                    newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                    newBoard.dividirViajeEnDos(i, k);
                    retval.add(new Successor("Dividir viaje " + k + " en camión " + i, newBoard));

                    // Swap genérico (permite idx 0<->1, pero queda abierto si hubiese más)
                    for (int a = 0; a < 2; a++) {
                        for (int b = a + 1; b < 2; b++) {
                            estadoCopy = deepCopyEstado(estado);
                            newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                            newBoard.swapPeticionesMismoViaje(i, k, a, b);
                            retval.add(new Successor("Swap peticiones en viaje " + k + " índices " + a + "<->" + b + " en camión " + i, newBoard));
                        }
                    }
                }
            }
        }

        // MOVER PETICIÓN ENTRE VIAJES: para cada camión, mueve cada petición de un viaje a otro viaje del mismo camión
        for (int i = 0; i < n; i++) {
            Camion camion = camiones.get(i);
            List<Viajes> viajes = camion.getViajes();
            if (viajes == null || viajes.size() < 2) continue; // necesita al menos dos viajes

            for (int origen = 0; origen < viajes.size(); origen++) {
                Viajes vOrigen = viajes.get(origen);
                // Buscar tramos no provisionales en el viaje origen
                ArrayList<Integer> indicesNoProvisionales = new ArrayList<>();
                for (int idx = 0; idx < vOrigen.getListaViajes().size(); idx++) {
                    if (!vOrigen.getListaViajes().get(idx).isProvisionalReturn()) indicesNoProvisionales.add(idx);
                }
                if (indicesNoProvisionales.size() == 0) continue;

                for (int destino = 0; destino < viajes.size(); destino++) {
                    if (origen == destino) continue;
                    for (int idxPeticion = 0; idxPeticion < indicesNoProvisionales.size(); idxPeticion++) {
                        Estado estadoCopy = deepCopyEstado(estado);
                        GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                        newBoard.moverPeticionEntreViajes(i, origen, idxPeticion, destino);
                        Successor s = new Successor("Mover petición " + idxPeticion + " de viaje " + origen + " a viaje " + destino + " en camión " + i, newBoard);
                        retval.add(s);
                    }
                }
            }
        }

        //System.out.println("[DEBUG] Vecinos generados: " + retval.size());
        return retval;

    }

    // Helper: deep copy Estado by copying camiones and their Viajes lists
    private Estado deepCopyEstado(Estado original) {
        List<Camion> newCamiones = new ArrayList<>();
        for (Camion c : original.getCamiones()) {
            Camion c2 = new Camion(c.getCoordX(), c.getCoordY());
            try { c2.setDeposito(c.getDeposito()); } catch (Exception ignored) {}
            
            // IMPORTANTE: copiar distancia y horas
            c2.setDistanciaRecorrida(c.getDistanciaRecorrida());
            c2.setHorasTrabajadas(c.getHorasTrabajadas());

            List<Viajes> newViajesList = new ArrayList<>();
            if (c.getViajes() != null) {
                for (Viajes v : c.getViajes()) {
                    Viajes v2 = new Viajes(v.getListaViajes());
                    newViajesList.add(v2);
                }
            }
            c2.setViajes(newViajesList);
            newCamiones.add(c2);
        }
        return new Estado(newCamiones);
    }

}

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
    // Capturar multiconjunto de peticiones asignadas en el estado original (por dias pendientes)
    java.util.Map<Integer, Integer> originalAssigned = board.contarPeticionesAsignadas();
    // total count (sum of values)
    int originalTotalAssigned = 0;
    for (int v : originalAssigned.values()) originalTotalAssigned += v;
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
                    // Diagnostics: print counts before and after reasignar to detect why totals change
                    java.util.Map<Integer, Integer> beforeMap = newBoard.contarPeticionesAsignadas();
                    int beforeTotal = 0; for (int vv : beforeMap.values()) beforeTotal += vv;
                    // Print details of the viaje being moved
                    try {
                        Viajes vg = newBoard.getEstado_actual().getCamiones().get(i).getViajes().get(k);
                        StringBuilder sb = new StringBuilder();
                        sb.append("[TRACE_REASIGN] VIAJEGROUP TO MOVE (origin=").append(i).append(" idx=").append(k).append(": ");
                        for (int tt = 0; tt < vg.getListaViajes().size(); tt++) {
                            Viaje t = vg.getListaViajes().get(tt);
                            sb.append("[#").append(tt).append(" d=").append(t.getDiasPendientes()).append(" prov=").append(t.isProvisionalReturn()).append(") ");
                        }
                        //System.out.println(sb.toString());
                    } catch (Exception e) {
                        //System.out.println("[TRACE_REASIGN] no viajeGroup available for origin=" + i + " k=" + k + " : " + e.getMessage());
                    }
                    //System.out.println("[TRACE_REASIGN] BEFORE reasignar: camion=" + i + "->" + j + " k=" + k + " counts=" + beforeMap + " total=" + beforeTotal);
                    newBoard.reasignarViajes(i, j, k);
                    java.util.Map<Integer, Integer> afterMap = newBoard.contarPeticionesAsignadas();
                    int afterTotal = 0; for (int vv : afterMap.values()) afterTotal += vv;
                    // Print details of the last viaje in destination after move
                    try {
                        List<Viajes> destList = newBoard.getEstado_actual().getCamiones().get(j).getViajes();
                        Viajes last = destList.get(destList.size()-1);
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("[TRACE_REASIGN] DEST LAST VIAJEGROUP (dest=").append(j).append(" idx=").append(destList.size()-1).append(": ");
                        for (int tt = 0; tt < last.getListaViajes().size(); tt++) {
                            Viaje t = last.getListaViajes().get(tt);
                            sb2.append("[#").append(tt).append(" d=").append(t.getDiasPendientes()).append(" prov=").append(t.isProvisionalReturn()).append(") ");
                        }
                        //System.out.println(sb2.toString());
                    } catch (Exception e) {
                        //System.out.println("[TRACE_REASIGN] no dest viajeGroup after move dest=" + j + " : " + e.getMessage());
                    }
                    //System.out.println("[TRACE_REASIGN] AFTER reasignar:  camion=" + i + "->" + j + " k=" + k + " counts=" + afterMap + " total=" + afterTotal);
                    String accion = "Reasignar viaje " + k + " de camión " + i + " a camión " + j;
                    // Diagnostics: compare totals before/after. Reasignar debería preservar el total.
                    java.util.Map<Integer, Integer> nowCounts_reasign = newBoard.contarPeticionesAsignadas();
                    if (newBoard.contarPeticionesAsignadas().equals(originalAssigned)) {
                        retval.add(new Successor(accion, newBoard));
                    } else {
                        //System.out.println("[WARNING] Reasignar viaje produciría cambio en multiconjunto; descartar: " + accion + " original=" + originalAssigned + " now=" + nowCounts_reasign);
                    }
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
                    Viajes viajeA = va.get(ia);
                    for (int ib = 0; ib < vb.size(); ib++) {
                        Viajes viajeB = vb.get(ib);
                        for (int t1=0; t1 < viajeA.getListaViajes().size(); t1++) {
                            for(int t2=0;t2<viajeB.getListaViajes().size();++t2){
                                Estado estadoCopy = deepCopyEstado(estado);
                                GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                                newBoard.intercambiaViajes(a, b, ia, ib, t1, t2);
                                String accionI = "Intercambiar viaje " + ia + " de camión " + a + " con viaje " + ib + " de camión " + b + " tramos " + t1 + "<->" + t2;
                                java.util.Map<Integer, Integer> nowCounts_interc = newBoard.contarPeticionesAsignadas();
                                if (newBoard.contarPeticionesAsignadas().equals(originalAssigned)) {
                                    retval.add(new Successor(accionI, newBoard));
                                } else {
                                    //System.out.println("[WARNING] Intercambio produciría cambio en multiconjunto; descartar: " + accionI + " original=" + originalAssigned + " now=" + nowCounts_interc);
                                }
                            }
                        }
                    }
                }
            }
        }
        

        // DIVIDIR y SWAP
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
                    GasolinaBoard newBoard;
                    // Dividir viaje en dos

                    Estado estadoCopy = deepCopyEstado(estado);
                    newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                    /*newBoard.dividirViajeEnDos(i, k);
                    String accionD = "Dividir viaje " + k + " en camión " + i;
                    java.util.Map<Integer, Integer> nowCounts_div = newBoard.contarPeticionesAsignadas();
                    if (newBoard.contarPeticionesAsignadas().equals(originalAssigned)) {
                        retval.add(new Successor(accionD, newBoard));
                    } else {
                        //System.out.println("[WARNING] Dividir viaje produciría cambio en multiconjunto; descartar: " + accionD + " original=" + originalAssigned + " now=" + nowCounts_div);
                    }*/

                    // Swap genérico (permite idx 0<->1, pero queda abierto si hubiese más)
                    //estadoCopy = deepCopyEstado(estado);
                    //newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                    newBoard.swapPeticionesMismoViaje(i, k);
                    String accionS = "Swap peticiones en viaje " + k + " índices 0<->1 en camión " + i;
                    java.util.Map<Integer, Integer> nowCounts_swap = newBoard.contarPeticionesAsignadas();
                    if (newBoard.contarPeticionesAsignadas().equals(originalAssigned)) {
                        retval.add(new Successor(accionS, newBoard));
                    } else {
                        //System.out.println("[WARNING] Swap dentro de viaje produciría cambio en multiconjunto; descartar: " + accionS + " original=" + originalAssigned + " now=" + nowCounts_swap);
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
                if (indicesNoProvisionales.isEmpty()) continue;
                for (int destino = 0; destino < viajes.size(); destino++) {
                    if (origen == destino) continue;
                    for (int idxPeticion = 0; idxPeticion < indicesNoProvisionales.size(); idxPeticion++) {
                        Estado estadoCopy = deepCopyEstado(estado);
                        GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                        newBoard.moverPeticionEntreViajes(i, origen, idxPeticion, destino);
                        String accionM = "Mover petición " + idxPeticion + " de viaje " + origen + " a viaje " + destino + " en camión " + i;
                        java.util.Map<Integer, Integer> nowCounts_mov = newBoard.contarPeticionesAsignadas();
                        if (newBoard.contarPeticionesAsignadas().equals(originalAssigned)) {
                            retval.add(new Successor(accionM, newBoard));
                        } else {
                            //System.out.println("[WARNING] Mover petición produciría cambio en multiconjunto; descartar: " + accionM + " original=" + originalAssigned + " now=" + nowCounts_mov);
                        }
                    }
                }
            }
        }

    //System.out.println("[DEBUG_NOASIGN] original unassigned size=" + board.getPeticionesNoAsignadas().size() + " originalAssigned=" + originalAssigned);
    for(int a= 0; a<n;a++){
            Camion camion = camiones.get(a);
            List<GasolinaBoard.PeticionInfo> noAsign = board.getPeticionesNoAsignadas();
            for (int i = 0; i < noAsign.size(); i++) {
                Estado estadoCopy = deepCopyEstado(estado);
                GasolinaBoard newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                // Ensure the new board has the same peticiones assigned/unassigned lists
                newBoard.copyPeticionesFrom(board);
                boolean success = newBoard.asignarPeticionNoAsignadaA(a, i);
                if (success) {
                    String accion = "Asignar petición no asignada " + i + " al camión " + a;
                    //System.out.println("[DEBUG_OP_ATTEMPT] " + accion);
                    //System.out.println("[DEBUG_OP_COUNTS] original=" + originalAssigned + " now=" + newBoard.contarPeticionesAsignadas());
                    //System.out.println("[DEBUG_OP_OK] Sucesor aceptado: " + accion);
                    retval.add(new Successor(accion, newBoard));
                }
                List<Viajes> viajes = camion.getListaViajes();
                for (int j = 0; j < viajes.size(); j++) {
                    Viajes viaje = viajes.get(j);
                    for (int k = 0; k < viaje.getListaViajes().size(); k++) {
                        estadoCopy = deepCopyEstado(estado);
                        newBoard = new GasolinaBoard(estadoCopy, board.getGasolineras(), board.getCentros());
                        // copy petitions so swap operates on the same unassigned list
                        newBoard.copyPeticionesFrom(board);
                        success = newBoard.swapPeticionNoAsignada(a, j, k, i);
                        if (success) {
                            String accion = "Swap petición no asignada " + i + " con tramo " + k + " del viaje " + j + " en camión " + a;
                            //System.out.println("[DEBUG_OP_ATTEMPT] " + accion);
                            //System.out.println("[DEBUG_OP_COUNTS] original=" + originalAssigned + " now=" + newBoard.contarPeticionesAsignadas());
                            //System.out.println("[DEBUG_OP_OK] Sucesor aceptado: " + accion);
                            retval.add(new Successor(accion, newBoard));
                        }
                }
            }
            }
        }

        /*//System.out.println("[DEBUG] Vecinos generados: " + retval.size());
        for (Successor s : retval) {
            //System.out.println("  - " + s.getAction());
        }*/

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
                    // Deep-copy each Viaje inside Viajes to avoid shared Viaje objects between states
                    java.util.List<Viaje> clonedTramos = new java.util.ArrayList<>();
                    for (Viaje t : v.getListaViajes()) {
                        Viaje t2 = new Viaje(t.getCoordX_inicio(), t.getCoordY_inicio(), t.getCoordX_fin(), t.getCoordY_fin(), t.getDiasPendientes(), t.isProvisionalReturn());
                        clonedTramos.add(t2);
                    }
                    Viajes v2 = new Viajes(clonedTramos);
                    newViajesList.add(v2);
                }
            }
            c2.setViajes(newViajesList);
            newCamiones.add(c2);
        }
        return new Estado(newCamiones);
    }

}

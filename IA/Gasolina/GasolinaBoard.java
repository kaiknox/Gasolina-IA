package IA.Gasolina;

import java.util.List;
import java.util.ArrayList;
import IA.Gasolina.Gasolinera;
import IA.Gasolina.Distribucion;
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
    private List<Gasolinera> gasolineras;
    private List<Distribucion> centros;
    private List<PeticionInfo> peticionesAsignadas = new ArrayList<>();
    public int numPeticiones = 0;

    /* Constructor */
    public GasolinaBoard(Estado estado_inicial, java.util.List<Gasolinera> gasolineras, java.util.List<Distribucion> centros) {
        this.estado_actual = estado_inicial;
        this.gasolineras = gasolineras;
        this.centros = centros;
    }

    /* OPERADORES */
    // Reasigna un viaje de un camion a otro
    /**
     * Reasigna UNA petición (primera encontrada en el viaje especificado) del camión origen al destino.
     * Regenera los Viajes de ambos camiones desde cero.
     */
    public void reasignarViajes(int idCamionOrigen, int idCamionDestino, int idViaje) {
        if (idCamionOrigen == idCamionDestino) return; // no hace nada si son el mismo
        
        Camion camionOrigen = estado_actual.getCamiones().get(idCamionOrigen);
        Camion camionDestino = estado_actual.getCamiones().get(idCamionDestino);
        
        List<Viajes> viajesOrigen = camionOrigen.getViajes();
        if (idViaje >= viajesOrigen.size()) return; // índice inválido
        
        Viajes viajeGrupo = viajesOrigen.get(idViaje);
        if (viajeGrupo == null || viajeGrupo.getListaViajes().isEmpty()) return;
        
        System.out.println("[DEBUG_OP] reasignarAntes: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());
        viajesOrigen.remove(idViaje);
        estado_actual.getCamiones().get(idCamionOrigen).setViajes(viajesOrigen);
        estado_actual.getCamiones().get(idCamionDestino).addViaje(viajeGrupo);
        camionOrigen.fixViajes();
        camionDestino.fixViajes();
        System.out.println("[DEBUG_OP] reasignarDespues: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());
        // Encontrar la primera gasolinera en este Viajes (ignorar tramos de retorno)
        /*Gasolinera gasolineraAMover = null;
        int diasPendientesAMover = -1;

        for (Viaje tramo : viajeGrupo.getListaViajes()) {
            if (tramo.isProvisionalReturn()) continue; // ignorar retornos

            // Buscar esta coordenada en la lista global de gasolineras
            for (int g = 0; g < gasolineras.size(); g++) {
                Gasolinera gas = gasolineras.get(g);
                if (gas.getCoordX() == tramo.getCoordX_fin() && gas.getCoordY() == tramo.getCoordY_fin()) {
                    gasolineraAMover = gas;
                    diasPendientesAMover = tramo.getDiasPendientes();
                    break;
                }
            }
            if (gasolineraAMover != null) break; // encontramos una
        }

        if (gasolineraAMover == null || diasPendientesAMover < 0) return;

        // Reconstruir ambos camiones desde cero: usar el valor real de días pendientes
        reconstruirCamionSinPeticion(camionOrigen, gasolineraAMover, diasPendientesAMover);
        agregarPeticionACamion(camionDestino, gasolineraAMover, diasPendientesAMover);*/
    }

    /**
     * Intercambia dos peticiones entre dos camiones diferentes.
     * Extrae las peticiones, las intercambia, y reconstruye ambos camiones.
     */
    public void intercambiaViajes(int idCamionA, int idCamionB, int idViajeA, int idViajeB, int idtramoA, int idtramoB) {
        // Operación segura a nivel de peticiones: extraer peticiones, intercambiar las dos
        // peticiones identificadas por (viaje,index) y reconstruir ambos camiones.
        if (idCamionA == idCamionB) return; // no intercambiar del mismo camión
        Camion camionA = estado_actual.getCamiones().get(idCamionA);
        Camion camionB = estado_actual.getCamiones().get(idCamionB);

        List<Viajes> viajesA = camionA.getViajes();
        List<Viajes> viajesB = camionB.getViajes();
        if (idViajeA >= viajesA.size() || idViajeB >= viajesB.size()) return;

        Viajes viajeGrupoA = viajesA.get(idViajeA);
        Viajes viajeGrupoB = viajesB.get(idViajeB);
        if (viajeGrupoA == null || viajeGrupoB == null) return;

        // localizar las dos peticiones (tramos no provisionales) por coordenadas y dias
        if (idtramoA < 0 || idtramoA >= viajeGrupoA.getListaViajes().size() || idtramoB < 0 || idtramoB >= viajeGrupoB.getListaViajes().size()) return;
        Viaje tramoA = viajeGrupoA.getListaViajes().get(idtramoA);
        Viaje tramoB = viajeGrupoB.getListaViajes().get(idtramoB);
        if (tramoA == null || tramoB == null) return;

        System.out.println("[DEBUG_OP] intercambiarAntes: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());

        // Extraer listas de peticiones con mapeo a viaje index
        List<PeticionInfo> petA = extraerPeticiones(camionA);
        List<PeticionInfo> petB = extraerPeticiones(camionB);

        // Buscar en petA la petición que corresponde a tramoA (coordenadas y dias)
        PeticionInfo candidatoA = null;
        for (PeticionInfo p : petA) {
            if (p.gasolinera.getCoordX() == tramoA.getCoordX_fin() && p.gasolinera.getCoordY() == tramoA.getCoordY_fin() && p.diasPendientes == tramoA.getDiasPendientes()) {
                candidatoA = p; break;
            }
        }
        PeticionInfo candidatoB = null;
        for (PeticionInfo p : petB) {
            if (p.gasolinera.getCoordX() == tramoB.getCoordX_fin() && p.gasolinera.getCoordY() == tramoB.getCoordY_fin() && p.diasPendientes == tramoB.getDiasPendientes()) {
                candidatoB = p; break;
            }
        }

        if (candidatoA == null || candidatoB == null) {
            // Si no encontramos coincidencias exactas, abandonamos (no hacer cambios inestables)
            return;
        }

        // Intercambiar
        petA.remove(candidatoA);
        petB.remove(candidatoB);
        petA.add(candidatoB);
        petB.add(candidatoA);

        // Reconstruir camiones
        reconstruirCamionConPeticiones(camionA, petA);
        reconstruirCamionConPeticiones(camionB, petB);

        System.out.println("[DEBUG_OP] intercambiarDespues: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());

        /*if (viajeGrupoA == null || viajeGrupoB == null) return;
        
        // Encontrar primera gasolinera de cada Viajes
        Gasolinera gasA = null, gasB = null;
        
        for (Viaje tramo : viajeGrupoA.getListaViajes()) {
            if (!tramo.isProvisionalReturn()) {
                for (Gasolinera g : gasolineras) {
                    if (g.getCoordX() == tramo.getCoordX_fin() && g.getCoordY() == tramo.getCoordY_fin()) {
                        gasA = g;
                        break;
                    }
                }
                if (gasA != null) break;
            }
        }
        
        for (Viaje tramo : viajeGrupoB.getListaViajes()) {
            if (!tramo.isProvisionalReturn()) {
                for (Gasolinera g : gasolineras) {
                    if (g.getCoordX() == tramo.getCoordX_fin() && g.getCoordY() == tramo.getCoordY_fin() && tramo.getDiasPendientes() == ) {
                        gasB = g;
                        break;
                    }
                }
                if (gasB != null) break;
            }
        }
        
        if (gasA == null || gasB == null) return;
        
        // Reconstruir ambos camiones intercambiando las peticiones
        List<PeticionInfo> peticionesA = extraerPeticiones(camionA);
        List<PeticionInfo> peticionesB = extraerPeticiones(camionB);
        
        // Eliminar las peticiones a intercambiar
        PeticionInfo petA = null, petB = null;
        for (PeticionInfo p : peticionesA) {
            if (p.gasolinera == gasA) {
                petA = p;
                break;
            }
        }
        for (PeticionInfo p : peticionesB) {
            if (p.gasolinera == gasB) {
                petB = p;
                break;
            }
        }
        
        if (petA == null || petB == null) return;
        
        peticionesA.remove(petA);
        peticionesB.remove(petB);
        peticionesA.add(petB);
        peticionesB.add(petA);
        
        // Reconstruir camiones
        reconstruirCamionConPeticiones(camionA, peticionesA);
        reconstruirCamionConPeticiones(camionB, peticionesB);*/
    }

    /**
     * Swap de peticiones dentro del mismo viaje: cambia el orden de dos paradas (no provisionales).
     * idxA y idxB son índices dentro de las paradas no provisionales (0..n-1). Con nuestro modelo, n suele ser 2.
     */
    public void swapPeticionesMismoViaje(int idCamion, int idViaje) { //He quitado idxA, idxB porque solo se puede hacer swap con 2 gasolineras y siempre son tramo 0 y 1
        int idxA = 0;
        int idxB = 1;
        Camion camion = estado_actual.getCamiones().get(idCamion);
        List<Viajes> viajes = camion.getViajes();
        if (idViaje < 0 || idViaje >= viajes.size()) return;
        Viajes viajeGrupo = viajes.get(idViaje);
        if (viajeGrupo == null) return;

        List<Viaje> tramos = viajeGrupo.getListaViajes();
        List<Viaje> gasTramos = new ArrayList<>();
        if (tramos.size() < 3) return; // nada que swapear
        for (Viaje v : tramos) if (!v.isProvisionalReturn()) gasTramos.add(v);
        if (gasTramos.size() < 2) return; // nada que swapear

        // Determinar las dos paradas en el nuevo orden
        Viaje stopA = gasTramos.get(idxA);
        Viaje stopB = gasTramos.get(idxB);

        int cx = camion.getCoordX();
        int cy = camion.getCoordY();
        // Reconstituir el viaje: centro -> stopA -> stopB -> centro
        Viaje tramo1 = new Viaje(cx, cy, stopB.getCoordX_fin(), stopB.getCoordY_fin(), stopB.getDiasPendientes());
        Viaje tramo2 = new Viaje(stopB.getCoordX_fin(), stopB.getCoordY_fin(), stopA.getCoordX_fin(), stopA.getCoordY_fin(), stopA.getDiasPendientes());
        Viaje tramo3 = new Viaje(stopA.getCoordX_fin(), stopA.getCoordY_fin(), cx, cy, stopA.getDiasPendientes(), true);

        // Sustituir en viajeGrupo
        List<Viaje> nuevo = new ArrayList<>();
        nuevo.add(tramo1);
        nuevo.add(tramo2);
        nuevo.add(tramo3);
        viajeGrupo.getListaViajes().clear();
        viajeGrupo.getListaViajes().addAll(nuevo);
        viajeGrupo.fixProvisionalReturn();
    }

    /**
     * Divide un viaje con dos gasolineras en dos viajes independientes (uno por gasolinera).
     * Valida: no exceder Camion.maxViajes, ni límites de distancia/horas.
     * Estructura esperada del viaje original: [centro->G1], [G1->G2], [G2->centro]
     */
    public void dividirViajeEnDos(int idCamion, int idViaje) {
        Camion camion = estado_actual.getCamiones().get(idCamion);
        List<Viajes> viajes = camion.getViajes();
        if (idViaje < 0 || idViaje >= viajes.size()) return;

        Viajes original = viajes.get(idViaje);
        if (original == null) return;
        List<Viaje> tramos = original.getListaViajes();
        if (tramos == null || tramos.size() < 3) return; // requiere 2 gasolineras

        // Identificar las dos gasolineras G1 (fin de tramo 0) y G2 (fin de tramo 1)
        Viaje leg0 = tramos.get(0); // centro -> G1
        Viaje leg1 = tramos.get(1); // G1 -> G2
        int cx = camion.getCoordX();
        int cy = camion.getCoordY();
        int g1x = leg0.getCoordX_fin();
        int g1y = leg0.getCoordY_fin();
        int g2x = leg1.getCoordX_fin();
        int g2y = leg1.getCoordY_fin();
        int d1 = leg0.getDiasPendientes();
        int d2 = leg1.getDiasPendientes();

    System.out.println("[DEBUG_OP] dividirAntes: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());
    // Validación de número de viajes: dividir añade +1 neto
        if (viajes.size() >= Camion.maxViajes) return;

        // Calcular nuevos totales (sustituir original por dos viajes A y B)
        double distA = distancia(cx, cy, g1x, g1y) + distancia(g1x, g1y, cx, cy);
        double distB = distancia(cx, cy, g2x, g2y) + distancia(g2x, g2y, cx, cy);
        double timeA = distA / Camion.VelocidadMedia;
        double timeB = distB / Camion.VelocidadMedia;
        double nuevoTotalDist = camion.getDistanciaRecorrida() - original.getDistanciaTotal() + distA + distB;
        double nuevoTotalHoras = camion.getHorasTrabajadas() - original.getTiempoTotal() + timeA + timeB;
        if (nuevoTotalDist > Camion.DistanciaMaxima || nuevoTotalHoras > Camion.HorasJornada) return;

        // Aplicar: intentar quitar original y construir dos viajes nuevos de forma "transaccional".
        // Guardamos estado para poder deshacer si algo falla al añadir los nuevos viajes.
        List<Viajes> backupViajes = new ArrayList<>(viajes);
        double backupDist = camion.getDistanciaRecorrida();
        double backupHoras = camion.getHorasTrabajadas();

        try {
            viajes.remove(idViaje);
            camion.setDistanciaRecorrida(camion.getDistanciaRecorrida() - original.getDistanciaTotal());
            camion.setHorasTrabajadas(camion.getHorasTrabajadas() - original.getTiempoTotal());

            // Viaje A: centro -> G1 -> centro
            Viajes vA = new Viajes();
            vA.añadirViaje(new Viaje(cx, cy, g1x, g1y, d1), camion);
            camion.addViaje(vA);

            // Viaje B: centro -> G2 -> centro
            Viajes vB = new Viajes();
            vB.añadirViaje(new Viaje(cx, cy, g2x, g2y, d2), camion);
            camion.addViaje(vB);
            camion.fixViajes();
        } catch (Exception ex) {
            // Revertir cambios si algo sale mal al construir/añadir los viajes
            System.out.println("[DEBUG_OP] dividir aborted: " + ex.getMessage());
            camion.setViajes(backupViajes);
            camion.setDistanciaRecorrida(backupDist);
            camion.setHorasTrabajadas(backupHoras);
            return;
        }
        System.out.println("[DEBUG_OP] dividirDespues: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());
    }

        /**
         * Mueve una petición (tramo no provisional) de un viaje a otro dentro del mismo camión.
         * idxPeticion es el índice dentro de los tramos no provisionales del viaje origen.
         */
        public void moverPeticionEntreViajes(int idCamion, int idViajeOrigen, int idxPeticion, int idViajeDestino) {
            Camion camion = estado_actual.getCamiones().get(idCamion);
            List<Viajes> viajes = camion.getViajes();
            if (idViajeOrigen >= viajes.size() || idViajeDestino >= viajes.size() || idViajeOrigen == idViajeDestino) return;

            System.out.println("[DEBUG_OP] moverAntes: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());

            // Extraer todas las peticiones del camión junto con su viaje origen (mapping)
            List<PeticionInfo> allPeticiones = new ArrayList<>();
            List<Integer> mappingViajeIdx = new ArrayList<>();
            for (int vi = 0; vi < viajes.size(); vi++) {
                Viajes vg = viajes.get(vi);
                if (vg == null) continue;
                for (Viaje t : vg.getListaViajes()) {
                    if (t.isProvisionalReturn()) continue;
                    // encontrar la gasolinera correspondiente
                    for (Gasolinera g : gasolineras) {
                        if (g.getCoordX() == t.getCoordX_fin() && g.getCoordY() == t.getCoordY_fin()) {
                            allPeticiones.add(new PeticionInfo(g, t.getDiasPendientes()));
                            mappingViajeIdx.add(vi);
                            break;
                        }
                    }
                }
            }

            // Localizar el índice global de la petición a mover (la idxPeticion'th no-provisional dentro del viaje origen)
            int occ = 0;
            int globalIdx = -1;
            for (int k = 0; k < mappingViajeIdx.size(); k++) {
                if (mappingViajeIdx.get(k) == idViajeOrigen) {
                    if (occ == idxPeticion) { globalIdx = k; break; }
                    occ++;
                }
            }
            if (globalIdx < 0 || globalIdx >= allPeticiones.size()) return; // nothing to move

            PeticionInfo toMove = allPeticiones.remove(globalIdx);
            mappingViajeIdx.remove(globalIdx);

            // Determinar índice de inserción global para el viaje destino: insertar al final del bloque de peticiones que pertenecen a idViajeDestino
            int insertAt = -1;
            int lastPos = -1;
            for (int k = 0; k < mappingViajeIdx.size(); k++) {
                if (mappingViajeIdx.get(k) == idViajeDestino) lastPos = k;
            }
            if (lastPos >= 0) {
                insertAt = lastPos + 1;
            } else {
                // No hay peticiones actualmente en el viaje destino: find first position where mappingViajeIdx > idViajeDestino
                insertAt = mappingViajeIdx.size();
                for (int k = 0; k < mappingViajeIdx.size(); k++) {
                    if (mappingViajeIdx.get(k) > idViajeDestino) { insertAt = k; break; }
                }
            }

            allPeticiones.add(insertAt, toMove);
            mappingViajeIdx.add(insertAt, idViajeDestino);

            // Reconstruir el camión con la nueva lista de peticiones
            reconstruirCamionConPeticiones(camion, allPeticiones);

            System.out.println("[DEBUG_OP] moverDespues: asignadas=" + contarPeticionesAsignadas() + " tam=" + contarPeticionesAsignadas().values().stream().mapToInt(Integer::intValue).sum());
        }
    // FUNCIONES AUXILIARES
    
    /**
     * Reconstruye un camión con una lista específica de peticiones
     */
    private void reconstruirCamionConPeticiones(Camion camion, List<PeticionInfo> peticiones) {
        camion.getViajes().clear();
        camion.setDistanciaRecorrida(0.0);
        camion.setHorasTrabajadas(0.0);
        
        for (PeticionInfo p : peticiones) {
            camion.addPeticion(p.gasolinera, p.diasPendientes);
        }
        camion.fixViajes();
    }


    /* Getters and setters */
    public Estado getEstado_actual() {
        return estado_actual;
    }

    /* Heuristic function */

    public double heuristic(){
        
        List<Camion> camiones = estado_actual.getCamiones();
        double beneficioTotal = 0.0;
        double distanciaTotal = 0.0;
        double perdidaTotal = 0.0;
        for (int i=0; i<camiones.size(); i++){
            beneficioTotal += camiones.get(i).getBeneficio();
            distanciaTotal += camiones.get(i).getDistanciaRecorrida();
            perdidaTotal += camiones.get(i).getPerdida();
        }
        
        // Hill Climbing MINIMIZA la heurística, así que:
        // heurística = distancia - beneficio
        // (queremos MINIMIZAR distancia y MAXIMIZAR beneficio)
        double heuristica = (perdidaTotal) + (distanciaTotal*2) - beneficioTotal; // Dist - beneficio * 2--> + beneficio perdido si lo dejamos para mañana

        System.out.println("[DEBUG] Heurística: " + heuristica + " (beneficio=" + beneficioTotal + ", dist=" + distanciaTotal + ", perdida=" + perdidaTotal + ")");
        return heuristica;
        
        //return -calcularBeneficio();
    }

    /* Goal test */

     public boolean is_goal(){ return false; } // --------------- no se si hace falta

     /* auxiliary functions */

     // Some functions will be needed for creating a copy of the state


    public void crearEstadoInicial(int funcionAescoger) {
        if(funcionAescoger == 1) {
            crearEstadoInicial1();
        }
        else if (funcionAescoger == 2) {
            crearEstadoInicial2();
        }
    }

    private void crearEstadoInicial1() {
        // Strategy 1: assign each gas station request to the nearest truck
        // Assumptions:
        // - There is a global list `Main.gasolineras` accessible which contains Gasolinera objects
        // - Each Gasolinera has getPeticiones() which returns ArrayList<Integer> (days pending)
        // - Each Camion has coordinates and addPeticion(Peticion) method

        // Defensive checks
        if (gasolineras == null || gasolineras.isEmpty() || estado_actual.getCamiones() == null || estado_actual.getCamiones().isEmpty()) return;

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
           
            if (peticiones == null) continue;
            numPeticiones += peticiones.size();

            for (int d = 0; d < peticiones.size(); d++) {
                int diasPendientes = peticiones.get(d);
                System.out.println("Asignando petición de días: " + diasPendientes + ", de gasolinera " + gIndex);
                // find nearest camion
                int bestCamion = -1;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < estado_actual.getCamiones().size(); c++) {
                    System.out.print("Evaluando camión " + c + " para gasolinera " + gIndex);
                    Camion camion = estado_actual.getCamiones().get(c);
                    double dist = distancia(camion.getCoordX(), camion.getCoordY(), g.getCoordX(), g.getCoordY());
                    System.out.println(", distancia = " + dist);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCamion = c;
                    }
                }

                if (bestCamion >= 0) {
                    System.out.println("-> Asignando petición a camión " + bestCamion + " con días pendientes " + diasPendientes);
                    peticionesAsignadas.add(new PeticionInfo(g, diasPendientes));
                    estado_actual.getCamiones().get(bestCamion).addPeticion(g, diasPendientes);
                    estado_actual.getCamiones().get(bestCamion).fixViajes();
                }
            }
        }
    }

    private void crearEstadoInicial2() {
        // Strategy 2: round-robin assign requests across trucks
        if (gasolineras == null || gasolineras.isEmpty() || estado_actual.getCamiones() == null || estado_actual.getCamiones().isEmpty()) return;

        int camionIndex = 0;
        int nCamiones = estado_actual.getCamiones().size();

        for (int gIndex = 0; gIndex < gasolineras.size(); gIndex++) {
            Gasolinera g = gasolineras.get(gIndex);
            java.util.ArrayList<Integer> peticiones = g.getPeticiones();
            if (peticiones == null) continue;
            numPeticiones += peticiones.size();
            for (int d = 0; d < peticiones.size(); d++) {
                int diasPendientes = peticiones.get(d);
                estado_actual.getCamiones().get(camionIndex).addPeticion(g, diasPendientes);
                peticionesAsignadas.add(new PeticionInfo(g, diasPendientes));
                camionIndex = (camionIndex + 1) % nCamiones;
            }
        }
    }


    private double distancia(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Reconstruye un camión eliminando una petición específica.
     * Extrae todas las peticiones actuales, elimina la especificada, y reconstruye desde cero.
     */
    private void reconstruirCamionSinPeticion(Camion camion, Gasolinera gasolineraAEliminar, int diasP) {
        // Extraer todas las peticiones actuales del camión
        List<PeticionInfo> peticionesActuales = extraerPeticiones(camion);
        
        // Eliminar la petición especificada
        PeticionInfo aEliminar = null;
        for (PeticionInfo p : peticionesActuales) {
            if (p.gasolinera == gasolineraAEliminar && p.diasPendientes == diasP) {
                aEliminar = p;
                break;
            }
        }
        if (aEliminar != null) {
            peticionesActuales.remove(aEliminar);
        }
        
        // Limpiar el camión y reconstruir
        camion.getViajes().clear();
        camion.setDistanciaRecorrida(0.0);
        camion.setHorasTrabajadas(0.0);
        
        // Re-agregar todas las peticiones restantes
        for (PeticionInfo p : peticionesActuales) {
            camion.addPeticion(p.gasolinera, p.diasPendientes);
        }
        camion.fixViajes();
    }
    
    /**
     * Agrega una petición a un camión (simplemente llama a addPeticion)
     */
    private void agregarPeticionACamion(Camion camion, Gasolinera gasolinera, int diasP) {
        camion.addPeticion(gasolinera, diasP);
    }
    
    /**
     * Extrae información de todas las peticiones asignadas a un camión
     */
    private List<PeticionInfo> extraerPeticiones(Camion camion) {
        List<PeticionInfo> result = new ArrayList<>();
        
        for (Viajes viajesGrupo : camion.getViajes()) {
            if (viajesGrupo == null) continue;
            
            for (Viaje tramo : viajesGrupo.getListaViajes()) {
                if (tramo.isProvisionalReturn()) continue; // ignorar retornos
                
                // Buscar la gasolinera correspondiente
                for (int g = 0; g < gasolineras.size(); g++) {
                    Gasolinera gas = gasolineras.get(g);
                    if (gas.getCoordX() == tramo.getCoordX_fin() && gas.getCoordY() == tramo.getCoordY_fin()) {
                        // Simplificación: asumimos índice 0 de petición
                        // En una implementación completa, necesitarías almacenar el índice en Viaje
                        result.add(new PeticionInfo(gas, tramo.getDiasPendientes()));
                        break;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Clase auxiliar para almacenar información de peticiones
     */
    private static class PeticionInfo {
        Gasolinera gasolinera;
        int diasPendientes;
        
        PeticionInfo(Gasolinera g, int dias) {
            this.gasolinera = g;
            this.diasPendientes = dias;
        }
    }

    public List<Gasolinera> getGasolineras() {
        return gasolineras;
    }

    public List<Distribucion> getCentros() {
        return centros;
    }

    //CONSULTORAS

    public void escribirEstadoActual() {
        /*System.out.println("Estado actual del GasolinaBoard:");
        for (Gasolinera gasolinera : gasolineras) {
            System.out.println("Gasolinera en (" + gasolinera.getCoordX() + "," + gasolinera.getCoordY() + ")");
            for(int Peticiones : gasolinera.getPeticiones()) {
                System.out.println("  Petición pendiente con días: " + Peticiones);
            }
        }*/
        List<Camion> camiones = estado_actual.getCamiones();
        for (int i = 0; i < camiones.size(); i++) {
            Camion camion = camiones.get(i);
            System.out.println("Camión " + i + ":");
            System.out.println("  Distancia recorrida: " + camion.getDistanciaRecorrida());
            System.out.println("  Horas trabajadas: " + camion.getHorasTrabajadas());
            List<Viajes> viajes = camion.getViajes();
            for (int j = 0; j < viajes.size(); j++) {
                Viajes viajeGrupo = viajes.get(j);
                System.out.println("    Viaje " + j + ":");
                System.out.println("      Distancia total: " + viajeGrupo.getDistanciaTotal());
                System.out.println("      Tiempo total: " + viajeGrupo.getTiempoTotal());
                List<Viaje> tramos = viajeGrupo.getListaViajes();
                for (int k = 0; k < tramos.size(); k++) {
                    Viaje tramo = tramos.get(k);
                    System.out.println("        Tramo " + k + ": (" + tramo.getCoordX_inicio() + "," + tramo.getCoordY_inicio() + ") -> (" + tramo.getCoordX_fin() + "," + tramo.getCoordY_fin() + "), Días pendientes: " + tramo.getDiasPendientes() + (tramo.isProvisionalReturn() ? " [Retorno Provisional]" : ""));
                }
            }
        }
    }

    /**
     * Calcula el beneficio: suma de lo ganado por peticiones atendidas menos el coste de los kilómetros recorridos.
     * Ganancia: para cada petición atendida, según días pendientes.
     * Coste: distancia total recorrida * coste por km (2).
     */
    public double calcularBeneficio() {
        double ganancia = 0.0;
        double distanciaTotal = 0.0;
        List<Camion> camiones = estado_actual.getCamiones();
        for (Camion camion : camiones) {
            List<Viajes> viajes = camion.getViajes();
            for (Viajes viajeGrupo : viajes) {
                List<Viaje> tramos = viajeGrupo.getListaViajes();
                for (Viaje tramo : tramos) {
                    if (!tramo.isProvisionalReturn()) {
                        int dias = tramo.getDiasPendientes();
                        if (dias == 0) {
                            ganancia += 1000 * 1.02;
                        } else {
                            ganancia += 1000 * ((100.0 - (Math.pow(2,dias))) / 100.0);
                        }
                    }
                }
            }
            distanciaTotal += camion.getDistanciaRecorrida();
        }
        double coste = distanciaTotal * 2.0;
        return ganancia - coste;
    }

    /**
     * Calcula la ganancia total por peticiones ignorando el coste por distancia.
     * Esto permite verificar que la suma de ganancias por días pendientes se mantiene
     * independientemente de la asignación entre camiones.
     */
    public double calcularGananciaSinCoste() {
        double ganancia = 0.0;
        List<Camion> camiones = estado_actual.getCamiones();
        for (Camion camion : camiones) {
            List<Viajes> viajes = camion.getViajes();
            for (Viajes viajeGrupo : viajes) {
                List<Viaje> tramos = viajeGrupo.getListaViajes();
                for (Viaje tramo : tramos) {
                    if (!tramo.isProvisionalReturn()) {
                        int dias = tramo.getDiasPendientes();
                        if (dias == 0) {
                            ganancia += 1000 * 1.02;
                        } else {
                            ganancia += 1000 * ((100.0 - (Math.pow(2, dias))) / 100.0);
                        }
                    }
                }
            }
        }
        return ganancia;
    }

    /**
     * Verifica que la multiconjunto de peticiones (por días pendientes) asignadas
     * en el board coincide con la multiconjunto original definido en la lista de gasolineras.
     * Devuelve true si coinciden (mismo número total y misma distribución por días),
     * o false si hay discrepancias. Imprime un resumen si hay diferencia.
     */
    public boolean verificarIntegridadPeticiones() {
        java.util.Map<Integer, Integer> asignadas = new java.util.HashMap<>();
        int totalAsignadas = 0;
        for (Camion camion : estado_actual.getCamiones()) {
            for (Viajes v : camion.getViajes()) {
                for (Viaje t : v.getListaViajes()) {
                    if (t.isProvisionalReturn()) continue;
                    int d = t.getDiasPendientes();
                    asignadas.put(d, asignadas.getOrDefault(d, 0) + 1);
                    totalAsignadas++;
                }
            }
        }

        java.util.Map<Integer, Integer> originales = new java.util.HashMap<>();
        int totalOriginales = 0;
        if (gasolineras != null) {
            for (Gasolinera g : gasolineras) {
                java.util.List<Integer> pet = g.getPeticiones();
                if (pet == null) continue;
                for (Integer d : pet) {
                    originales.put(d, originales.getOrDefault(d, 0) + 1);
                    totalOriginales++;
                }
            }
        }

        boolean igual = (totalAsignadas == totalOriginales) && asignadas.equals(originales);
        if (!igual) {
            System.out.println("[INTEGRITY] Discrepancia en peticiones detectada:");
            System.out.println("  Total asignadas: " + totalAsignadas + ", total originales: " + totalOriginales);
            System.out.println("  Distribucion asignadas: " + asignadas);
            System.out.println("  Distribucion originales: " + originales);
        } else {
            System.out.println("[INTEGRITY] Multiconjunto de peticiones OK: total=" + totalAsignadas);
        }

        return igual;
    }

    /**
     * Cuenta las peticiones actualmente asignadas en el board por días pendientes.
     * Devuelve un Map donde la clave es diasPendientes y el valor el número de peticiones asignadas con ese valor.
     */
    public java.util.Map<Integer, Integer> contarPeticionesAsignadas() {
        java.util.Map<Integer, Integer> asignadas = new java.util.HashMap<>();
        for (Camion camion : estado_actual.getCamiones()) {
            for (Viajes v : camion.getViajes()) {
                for (Viaje t : v.getListaViajes()) {
                    if (t.isProvisionalReturn()) continue;
                    int d = t.getDiasPendientes();
                    asignadas.put(d, asignadas.getOrDefault(d, 0) + 1);
                }
            }
        }
        return asignadas;
    }

    public void imprimirEstadoPeticiones(){
        System.out.println("Hay un total de: " + numPeticiones + " y asignadas: "+ peticionesAsignadas.size());
    }
}
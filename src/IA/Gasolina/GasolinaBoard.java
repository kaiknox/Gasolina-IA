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
        
        // Encontrar la primera gasolinera en este Viajes (ignorar tramos de retorno)
        Gasolinera gasolineraAMover = null;
        int indicePeticion = -1;
        
        for (Viaje tramo : viajeGrupo.getListaViajes()) {
            if (tramo.isProvisionalReturn()) continue; // ignorar retornos
            
            // Buscar esta coordenada en la lista global de gasolineras
            for (int g = 0; g < gasolineras.size(); g++) {
                Gasolinera gas = gasolineras.get(g);
                if (gas.getCoordX() == tramo.getCoordX_fin() && gas.getCoordY() == tramo.getCoordY_fin()) {
                    gasolineraAMover = gas;
                    // Asumir que es la primera petición de esta gasolinera en este viaje
                    indicePeticion = 0; // simplificación: tomamos la primera petición
                    break;
                }
            }
            if (gasolineraAMover != null) break; // encontramos una
        }
        
        if (gasolineraAMover == null || indicePeticion < 0) return;
        
        // Reconstruir ambos camiones desde cero
        reconstruirCamionSinPeticion(camionOrigen, gasolineraAMover, indicePeticion);
        agregarPeticionACamion(camionDestino, gasolineraAMover, indicePeticion);
    }

    /**
     * Intercambia dos peticiones entre dos camiones diferentes.
     * Extrae las peticiones, las intercambia, y reconstruye ambos camiones.
     */
    public void intercambiaViajes(int idCamionA, int idCamionB, int idViajeA, int idViajeB) {
        if (idCamionA == idCamionB) return; // no intercambiar del mismo camión
        
        Camion camionA = estado_actual.getCamiones().get(idCamionA);
        Camion camionB = estado_actual.getCamiones().get(idCamionB);

        List<Viajes> viajesA = camionA.getViajes();
        List<Viajes> viajesB = camionB.getViajes();
        
        if (idViajeA >= viajesA.size() || idViajeB >= viajesB.size()) return;

        Viajes viajeGrupoA = viajesA.get(idViajeA);
        Viajes viajeGrupoB = viajesB.get(idViajeB);
        
        if (viajeGrupoA == null || viajeGrupoB == null) return;
        
        // Encontrar primera gasolinera de cada Viajes
        Gasolinera gasA = null, gasB = null;
        int indiceA = 0, indiceB = 0;
        
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
                    if (g.getCoordX() == tramo.getCoordX_fin() && g.getCoordY() == tramo.getCoordY_fin()) {
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
            if (p.gasolinera == gasA && p.indicePeticion == indiceA) {
                petA = p;
                break;
            }
        }
        for (PeticionInfo p : peticionesB) {
            if (p.gasolinera == gasB && p.indicePeticion == indiceB) {
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
        reconstruirCamionConPeticiones(camionB, peticionesB);
    }

    /**
     * Invierte el orden de visita de las gasolineras en un viaje concreto de un camión.
     * Solo aplica si el viaje tiene exactamente dos gasolineras (tramos no provisionales).
     */
    public void invertirOrdenViaje(int idCamion, int idViaje) {
        Camion camion = estado_actual.getCamiones().get(idCamion);
        List<Viajes> viajes = camion.getViajes();
        if (idViaje >= viajes.size()) return;
        Viajes viajeGrupo = viajes.get(idViaje);
        List<Viaje> tramos = viajeGrupo.getListaViajes();
        // Buscar los dos tramos de gasolinera (no provisionales)
        List<Viaje> gasTramos = new ArrayList<>();
        for (Viaje v : tramos) {
            if (!v.isProvisionalReturn()) {
                gasTramos.add(v);
            }
        }
        if (gasTramos.size() != 2) return; // solo si hay dos gasolineras

        // Invertir el orden
        Viaje primero = gasTramos.get(0);
        Viaje segundo = gasTramos.get(1);

        // Reconstruir el viaje: centro -> segundo -> primero -> centro
        int cx = camion.getCoordX();
        int cy = camion.getCoordY();
        int d1 = segundo.getDiasPendientes();
        int d2 = primero.getDiasPendientes();
        // Tramo 1: centro -> segunda gasolinera
        Viaje tramo1 = new Viaje(cx, cy, segundo.getCoordX_fin(), segundo.getCoordY_fin(), d1);
        // Tramo 2: segunda -> primera gasolinera
        Viaje tramo2 = new Viaje(segundo.getCoordX_fin(), segundo.getCoordY_fin(), primero.getCoordX_fin(), primero.getCoordY_fin(), d2);
        // Tramo 3: primera gasolinera -> centro
        Viaje tramo3 = new Viaje(primero.getCoordX_fin(), primero.getCoordY_fin(), cx, cy, d2, true);

        // Actualizar el objeto Viajes
        List<Viaje> nuevoOrden = new ArrayList<>();
        nuevoOrden.add(tramo1);
        nuevoOrden.add(tramo2);
        nuevoOrden.add(tramo3);
        viajeGrupo.getListaViajes().clear();
        viajeGrupo.getListaViajes().addAll(nuevoOrden);

        // Recalcular distancia y tiempo
        double nuevaDist = tramo1.getDistanciaTotal() + tramo2.getDistanciaTotal() + tramo3.getDistanciaTotal();
        double nuevoTiempo = tramo1.getTiempoTotal() + tramo2.getTiempoTotal() + tramo3.getTiempoTotal();
        viajeGrupo.setDistanciaTotal(nuevaDist);
        viajeGrupo.setTiempoTotal(nuevoTiempo);

        // Actualizar totales del camión
        double suma = 0.0, sumaT = 0.0;
        for (Viajes v : viajes) {
            suma += v.getDistanciaTotal();
            sumaT += v.getTiempoTotal();
        }
        camion.setDistanciaRecorrida(suma);
        camion.setHorasTrabajadas(sumaT);
    }

    /**
     * Swap de peticiones dentro del mismo viaje: cambia el orden de dos paradas (no provisionales).
     * idxA y idxB son índices dentro de las paradas no provisionales (0..n-1). Con nuestro modelo, n suele ser 2.
     */
    public void swapPeticionesMismoViaje(int idCamion, int idViaje, int idxA, int idxB) {
        if (idxA == idxB) return;
        Camion camion = estado_actual.getCamiones().get(idCamion);
        List<Viajes> viajes = camion.getViajes();
        if (idViaje < 0 || idViaje >= viajes.size()) return;
        Viajes viajeGrupo = viajes.get(idViaje);
        if (viajeGrupo == null) return;

        List<Viaje> tramos = viajeGrupo.getListaViajes();
        List<Viaje> gasTramos = new ArrayList<>();
        for (Viaje v : tramos) if (!v.isProvisionalReturn()) gasTramos.add(v);
        if (gasTramos.size() < 2) return; // nada que swapear
        if (idxA < 0 || idxB < 0 || idxA >= gasTramos.size() || idxB >= gasTramos.size()) return;

        // Determinar las dos paradas en el nuevo orden
        Viaje stopA = gasTramos.get(idxA);
        Viaje stopB = gasTramos.get(idxB);

        int cx = camion.getCoordX();
        int cy = camion.getCoordY();
        // Reconstituir el viaje: centro -> stopA -> stopB -> centro
        Viaje tramo1 = new Viaje(cx, cy, stopA.getCoordX_fin(), stopA.getCoordY_fin(), stopA.getDiasPendientes());
        Viaje tramo2 = new Viaje(stopA.getCoordX_fin(), stopA.getCoordY_fin(), stopB.getCoordX_fin(), stopB.getCoordY_fin(), stopB.getDiasPendientes());
        Viaje tramo3 = new Viaje(stopB.getCoordX_fin(), stopB.getCoordY_fin(), cx, cy, stopB.getDiasPendientes(), true);

        // Sustituir en viajeGrupo
        List<Viaje> nuevo = new ArrayList<>();
        nuevo.add(tramo1);
        nuevo.add(tramo2);
        nuevo.add(tramo3);
        viajeGrupo.getListaViajes().clear();
        viajeGrupo.getListaViajes().addAll(nuevo);

        double nuevaDist = tramo1.getDistanciaTotal() + tramo2.getDistanciaTotal() + tramo3.getDistanciaTotal();
        double nuevoTiempo = tramo1.getTiempoTotal() + tramo2.getTiempoTotal() + tramo3.getTiempoTotal();
        viajeGrupo.setDistanciaTotal(nuevaDist);
        viajeGrupo.setTiempoTotal(nuevoTiempo);

        // Actualizar totales del camión
        double suma = 0.0, sumaT = 0.0;
        for (Viajes v : viajes) {
            suma += v.getDistanciaTotal();
            sumaT += v.getTiempoTotal();
        }
        camion.setDistanciaRecorrida(suma);
        camion.setHorasTrabajadas(sumaT);
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

        // Aplicar: quitar original y construir dos viajes nuevos
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
    }

        /**
         * Mueve una petición (tramo no provisional) de un viaje a otro dentro del mismo camión.
         * idxPeticion es el índice dentro de los tramos no provisionales del viaje origen.
         */
        public void moverPeticionEntreViajes(int idCamion, int idViajeOrigen, int idxPeticion, int idViajeDestino) {
            Camion camion = estado_actual.getCamiones().get(idCamion);
            List<Viajes> viajes = camion.getViajes();
            if (idViajeOrigen >= viajes.size() || idViajeDestino >= viajes.size() || idViajeOrigen == idViajeDestino) return;
            Viajes origen = viajes.get(idViajeOrigen);
            Viajes destino = viajes.get(idViajeDestino);
            List<Viaje> tramosOrigen = origen.getListaViajes();
            // Buscar los tramos no provisionales en origen
            ArrayList<Integer> indicesNoProvisionales = new ArrayList<>();
            for (int i = 0; i < tramosOrigen.size(); i++) {
                if (!tramosOrigen.get(i).isProvisionalReturn()) indicesNoProvisionales.add(i);
            }
            if (idxPeticion >= indicesNoProvisionales.size()) return;
            int idxTramo = indicesNoProvisionales.get(idxPeticion);
            Viaje tramoMover = tramosOrigen.get(idxTramo);

            // Eliminar el tramo del viaje origen
            tramosOrigen.remove(idxTramo);
            // Recalcular distancia/tiempo del viaje origen
            double distO = 0.0, tiempoO = 0.0;
            for (Viaje v : tramosOrigen) {
                distO += v.getDistanciaTotal();
                tiempoO += v.getTiempoTotal();
            }
            origen.setDistanciaTotal(distO);
            origen.setTiempoTotal(tiempoO);

            // Intentar añadir el tramo al viaje destino
            if (destino.puedeAñadir()) {
                destino.getListaViajes().add(tramoMover);
                // Recalcular distancia/tiempo del viaje destino
                double distD = 0.0, tiempoD = 0.0;
                for (Viaje v : destino.getListaViajes()) {
                    distD += v.getDistanciaTotal();
                    tiempoD += v.getTiempoTotal();
                }
                destino.setDistanciaTotal(distD);
                destino.setTiempoTotal(tiempoD);
            } else {
                // Si no cabe, devolver el tramo al origen
                tramosOrigen.add(idxTramo, tramoMover);
                origen.setDistanciaTotal(distO + tramoMover.getDistanciaTotal());
                origen.setTiempoTotal(tiempoO + tramoMover.getTiempoTotal());
            }

            // Actualizar totales del camión
            double suma = 0.0, sumaT = 0.0;
            for (Viajes v : viajes) {
                suma += v.getDistanciaTotal();
                sumaT += v.getTiempoTotal();
            }
            camion.setDistanciaRecorrida(suma);
            camion.setHorasTrabajadas(sumaT);
        }


    public void fusionarViajes(int idCamion, int idViajeA, int idViajeB) {
        if (idViajeA == idViajeB) return; // no fusionar el mismo viaje
        Camion camion = estado_actual.getCamiones().get(idCamion);
        List<Viajes> viajes = camion.getViajes();
        if (idViajeA >= viajes.size() || idViajeB >= viajes.size()) return; // índices inválidos

        Viajes viajeA = viajes.get(idViajeA);
        Viajes viajeB = viajes.get(idViajeB);
        // Verificar si la fusión es posible (capacidad)
        int totalPeticiones = viajeA.getListaViajes().size() + viajeB.getListaViajes().size();
        if (totalPeticiones > 3) return; // no se puede fusionar  

        // Realizar la fusión
        viajeA.getListaViajes().addAll(viajeB.getListaViajes());
        viajeA.setDistanciaTotal(viajeA.getDistanciaTotal() + viajeB.getDistanciaTotal());
        viajeA.setTiempoTotal(viajeA.getTiempoTotal() + viajeB.getTiempoTotal());
        // Eliminar el viaje B
        viajes.remove(idViajeB);
        // Actualizar totales del camión
        double suma = 0.0, sumaT = 0.0;
        for (Viajes v : viajes) {
            suma += v.getDistanciaTotal();
            sumaT += v.getTiempoTotal();
        }
        camion.setDistanciaRecorrida(suma);
        camion.setHorasTrabajadas(sumaT);
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
            camion.addPeticion(p.gasolinera, p.indicePeticion);
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
        double distanciaTotal = 0.0;
        for (int i=0; i<camiones.size(); i++){
            beneficioTotal += camiones.get(i).getBeneficio();
            distanciaTotal += camiones.get(i).getDistanciaRecorrida();
        }
        
        // Hill Climbing MINIMIZA la heurística, así que:
        // heurística = distancia - beneficio
        // (queremos MINIMIZAR distancia y MAXIMIZAR beneficio)
        double lambda = 0.5; // peso para la distancia
        double heuristica = distanciaTotal - (beneficioTotal / lambda);
        
        //System.out.println("[DEBUG] Heurística: " + heuristica + " (beneficio=" + beneficioTotal + ", dist=" + distanciaTotal + ")");
        return heuristica;
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

            for (int d = 0; d < peticiones.size(); d++) {
                // find nearest camion
                int bestCamion = -1;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < estado_actual.getCamiones().size(); c++) {
                    Camion camion = estado_actual.getCamiones().get(c);
                    double dist = distancia(camion.getCoordX(), camion.getCoordY(), g.getCoordX(), g.getCoordY());
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCamion = c;
                    }
                }

                if (bestCamion >= 0) {
                    estado_actual.getCamiones().get(bestCamion).addPeticion(g, d);
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

            for (int d = 0; d < peticiones.size(); d++) {
                estado_actual.getCamiones().get(camionIndex).addPeticion(g, d);
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
    private void reconstruirCamionSinPeticion(Camion camion, Gasolinera gasolineraAEliminar, int indicePeticionAEliminar) {
        // Extraer todas las peticiones actuales del camión
        List<PeticionInfo> peticionesActuales = extraerPeticiones(camion);
        
        // Eliminar la petición especificada
        PeticionInfo aEliminar = null;
        for (PeticionInfo p : peticionesActuales) {
            if (p.gasolinera == gasolineraAEliminar && p.indicePeticion == indicePeticionAEliminar) {
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
            camion.addPeticion(p.gasolinera, p.indicePeticion);
        }
    }
    
    /**
     * Agrega una petición a un camión (simplemente llama a addPeticion)
     */
    private void agregarPeticionACamion(Camion camion, Gasolinera gasolinera, int indicePeticion) {
        camion.addPeticion(gasolinera, indicePeticion);
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
                        result.add(new PeticionInfo(gas, 0, tramo.getDiasPendientes()));
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
        int indicePeticion;
        int diasPendientes;
        
        PeticionInfo(Gasolinera g, int idx, int dias) {
            this.gasolinera = g;
            this.indicePeticion = idx;
            this.diasPendientes = dias;
        }
    }

    public List<Gasolinera> getGasolineras() {
        return gasolineras;
    }

    public List<Distribucion> getCentros() {
        return centros;
    }
}

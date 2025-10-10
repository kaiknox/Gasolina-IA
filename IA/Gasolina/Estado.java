package IA.Gasolina;

public class Estado {
    
    list<Gasolinera> gasolineras;
    list<CentroDeDistribucion> centros;
    list<Camion> camiones;

    public void crearEstadoInicial(Gasolineras gasolineras) {
        int numGasolineras = 10;
        int numCentros = 10;
        int seed = 12345;
        Gasolineras gasolineras = new Gasolineras(numGasolineras, seed);
        CentrosDeDistribucion centros = new CentrosDeDistribucion(numCentros, 1, seed);

        for(int i = 0; i < numGasolineras; ++i) 
            Gasolinera gasolinera = gasolineras.getGasolinera(i);
        for(int i = 0; i < numCentros; ++i) {
            CentroDeDistribucion centro = centros.getCentro(i);
            camiones.add(new Camion(centro.getCoordX(), centro.getCoordY()));
        }

        ///////// Mas adelante hay que crear un estado inicial con una solución mejor.
    }
}

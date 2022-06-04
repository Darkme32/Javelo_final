package ch.epfl.javelo.routing;


import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static ch.epfl.javelo.routing.GpxGenerator.createGpx;
import static ch.epfl.javelo.routing.GpxGenerator.writeGpx;


public final class Stage6Test {
    public static void main(String[] args) throws IOException {
//        Graph g = Graph.loadFrom(Path.of("ch_west"));
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
//        Route r = rc.bestRouteBetween(2046055, 2694240 );
        Route r = rc.bestRouteBetween(159049, 117669);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
        KmlPrinter.write("javelo.kml", r);



//        writeGpx("testGPX",r,elevationProfile(r,1));
        writeGpx("testGPXShort",r,elevationProfile(r,1));
    }
}
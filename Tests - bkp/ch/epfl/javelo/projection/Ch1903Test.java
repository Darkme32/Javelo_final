package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.projection.Ch1903.*;
import static org.junit.jupiter.api.Assertions.*;

class Ch1903Test {

    private double lonFR = 7.153656  ;
    private double latFR = 46.806403 ;
    private int eFR   = 2578247   ;
    private int nFR   = 1183956   ;

    @Test
    void WsToChE(){
        assertEquals(eFR,e(lonFR,latFR),1);
    }

    @Test
    void WsToChN(){
        assertEquals(nFR,n(lonFR,latFR),1);
    }

    @Test
    void ChToWsLon(){
        assertEquals(lonFR,lon(eFR,nFR),1e-6);
    }

    @Test
    void ChToWsLat(){
        assertEquals(latFR,lat(eFR,nFR),3e-6);
    }


}
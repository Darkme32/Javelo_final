package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Functions.*;
import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    //@Test
    //void constantWorks() {
    //    double[] values =  {12.2,190201.218,Math.PI,2147483647};
    //    for(int i = 0; i< values.length; i++){
    //       Constant c = new Constant(values[i]);
    //        assertEquals(values[i], c.applyAsDouble(values[values.length-1-i]));
    //   }
    //}

    @Test
    void sampledTest() {
        float[] samplesValues =   {3, 3, 4, 6, 10, 18, 12,-4,   3};
        double[] xValues =        {0, 2, 4, 6,  9, 10, 12,14,  15, 16};
        double[] expectedValues = {3, 3, 4, 6, 14, 18, 12,-4,-0.5, 3};
        double xMax = 16;

        Sampled s = new Sampled(samplesValues, xMax);

        for (int i = 0; i < expectedValues.length; i++) {
           assertEquals(expectedValues[i], s.applyAsDouble(xValues[i]));
        }

    }


//    @Test
  //  void sampledIllegalArgument() {

   //     float[] samplesValues = {3, 3, 4, 6, 10, 18};
    //    double xValue = 9;
    //    double xMax = -1;
    //    Functions f = new Functions();
   //         assertThrows(IllegalArgumentException.class,()->{
  //              f.sampled(samplesValues, xMax);;});

  //  }

    //@Test
    //void applyAsDoubleTest() {
    //    Functions f = new Functions();

    //    double[] values = {12.2,190201.218,Math.PI,2147483647};
    //   double expected = 0;
    //    for(int i = 0; i<values.length; i++)
    //        assertEquals(expected,f.applyAsDouble(values[i]));
    //}}
}
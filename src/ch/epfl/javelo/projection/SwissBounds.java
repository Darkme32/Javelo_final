package ch.epfl.javelo.projection;
/**
 * Contains constants and methods related
 * to the limits of Switzerland.
 *
 * @author Imade Bouhamria (339659)
 */
public final class SwissBounds {

    private SwissBounds(){}

    /**
     * These values determine the size of the map
     * as well as a range of coordinates that are
     * considered valid, i.e. in Switzerland.
     */
    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Checks whether a specific point is in Switzerland
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return true or false
     */
    public static boolean containsEN(double e, double n){

        return e>=MIN_E && e<= MAX_E && n>= MIN_N && n<= MAX_N;
    }
}

package ch.epfl.javelo.routing;
/**
 * Cost function
 *
 * @author Imade Bouhamria (339659)
 */
public interface CostFunction {

    /**
     * returns the factor by which the length
     * of the identity edgeId, starting from
     * the identity nodeId, must be multiplied
     * @param nodeId the id of the node
     * @param edgeId the id of the edge
     * @return a factor that correspond to the cost
     */
    double costFactor(int nodeId, int edgeId);

}

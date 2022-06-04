package ch.epfl.javelo;
/**
 * Preconditions
 *
 * @author Imade Bouhamria (339659)
 */
public final class Preconditions {


    private Preconditions() {
    }

    /**
     * Checks whether a certain condition is met
     *
     * @param shouldBeTrue validity of a particular criteria
     * @throws IllegalArgumentException if the boolean is false, meaning that there is a variable not satisfying a specific condition.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }


}

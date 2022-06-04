package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Regroups the properties related
 * to the waypoints and the corresponding route.
 * @author Menzo Bouaissi (340377)
 */
public class ErrorManager {

    private final static double TRANSPARENT_TO_OPAQUE_DURATION = 0.2;
    private final static double OPAQUE_TO_TRANSPARENT_DURATION = 0.5;
    private final static double PAUSE_DURATION = 2;
    private final static double OPAQUE_MAX_VALUE = 0.8;
    private final static double OPAQUE_MIN_VALUE = 0;

    private final VBox errorBox;
    private final SequentialTransition transition;
    private final FadeTransition opaqueToTransparent,transparentToOpaque;
    private final PauseTransition pauseTransition;
    private final Text errorMessage;

    /**
     * Instantiates an error manager. We set
     * up the CSS file, the requested transition
     * and the pane.
     *
     */
    public ErrorManager() {
        errorBox = new VBox();
        errorBox.getStylesheets().add("error.css");
        errorBox.setMouseTransparent(true);

        errorMessage = new Text();
        errorMessage.setId("error.css");

        transparentToOpaque =
                new FadeTransition(Duration.seconds(TRANSPARENT_TO_OPAQUE_DURATION), errorBox);
        transparentToOpaque.setFromValue(OPAQUE_MIN_VALUE);
        transparentToOpaque.setToValue(OPAQUE_MAX_VALUE);

        pauseTransition = new PauseTransition(Duration.seconds(PAUSE_DURATION));

        opaqueToTransparent =
                new FadeTransition(Duration.seconds(OPAQUE_TO_TRANSPARENT_DURATION), errorBox);
        opaqueToTransparent.setFromValue(OPAQUE_MAX_VALUE);
        opaqueToTransparent.setToValue(OPAQUE_MIN_VALUE);

        transition = new SequentialTransition(transparentToOpaque,
                pauseTransition,
                opaqueToTransparent);

    }

    /**
     * Returns the pane
     * @return the pane
     */
    public Pane pane() {
        return errorBox;
    }

    /**
     * Displays the error in a small pane.
     * We just check whether there already
     * is an error displayed,
     * and if not, we stop it.
     * @param message the message to display
     */
    public void displayError(String message) {
        if (transition.getStatus().equals(Animation.Status.RUNNING))transition.stop();
        errorBox.getChildren().clear();
        errorMessage.setText(message);
        errorBox.getChildren().add(errorMessage);

        transition.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}

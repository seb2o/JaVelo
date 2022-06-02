package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Classe gérant l'affichage de messages d'erreur.
 */
public final class ErrorManager {
    private final Pane pane;
    private final Text text;
    private final SequentialTransition transition;

    /**
     * Constructeur de gestionnaire d'erreur.
     */
    public ErrorManager(){
        this.pane = new VBox();
        pane.getStylesheets().add("error.css");
        this.text = new Text();
        pane.getChildren().add(this.text);
        pane.setMouseTransparent(true);
        FadeTransition fadeTransitionOne = new FadeTransition(new Duration(200));
        fadeTransitionOne.setFromValue(0);
        fadeTransitionOne.setToValue(.8);
        PauseTransition pauseTransition = new PauseTransition(new Duration(2000));
        FadeTransition fadeTransitionTwo = new FadeTransition(new Duration(500));
        fadeTransitionTwo.setFromValue(.8);
        fadeTransitionTwo.setToValue(0);
        this.transition = new SequentialTransition(fadeTransitionOne,pauseTransition,fadeTransitionTwo);
        transition.nodeProperty().set(pane);
    }

    /**
     * @return le panneau, sur lequel apparaissent les messages d'erreur
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Affiche le message d'erreur et joue le son d'erreur, avec l'animation.
     * @param string le message d'erreur à afficher.
     */
    public void displayError(String string){
        transition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(string);
        transition.play();
    }
}
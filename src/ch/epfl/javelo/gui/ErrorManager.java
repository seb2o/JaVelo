package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {
    private Pane pane;
    private Text text;
    private SequentialTransition transition;

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

    public Pane pane() {
        return pane;
    }

    public void displayError(String string){
        transition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        text.setText(string);
        transition.play();
        System.out.println("kjlnk,l;");
    }
}
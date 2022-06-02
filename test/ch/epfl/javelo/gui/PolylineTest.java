package ch.epfl.javelo.gui;

// Java Program to create a open area
// of connected segments using polyline
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.scene.Group;
public class PolylineTest extends Application {

    // launch the application
    public void start(Stage stage)
    {
        // set title for the stage
        stage.setTitle("creating Polyline");

        // points
        double points[] = { 20.0d, 20.0d, 40.0d, 240.0d, 60.0d,
                180.0d, 80.0d, 200.0d, 100.0d, 90.0d };

        // create a polyline
        Polyline polyline = new Polyline(points);

        // create a Group
        Group group = new Group(polyline);

        // create a scene
        Scene scene = new Scene(group, 500, 300);

        // set the scene
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String args[])
    {
        // launch the application
        launch(args);
    }
}

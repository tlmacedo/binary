package br.com.tlmacedo.binary.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewPrincipal {
    static Stage stage;

    public void openViewPrincipal() throws IOException {
        setStage(new Stage());
        Parent parent;
        Scene scene = null;

        parent = FXMLLoader.load(getClass().getResource("/FxmlBinary.fxml"));
        scene = new Scene(parent);

        stage.setTitle("Binary by Thiago Macedo.");
//        stage.setResizable(false);
        stage.setScene(scene);

        setupStageLocation(stage, 1);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ViewPrincipal.stage = stage;
    }

    private void setupStageLocation(Stage stage, int screenNumber) {
        ObservableList<Screen> screens = Screen.getScreens();
        Screen screen = screens.size() <= screenNumber ? Screen.getPrimary() : screens.get(screenNumber);

        Rectangle2D bounds = screen.getBounds();
        boolean primary = screen.equals(Screen.getPrimary());    // WORKAROUND: this doesn't work nice in combination with full screen, so this hack is used to prevent going fullscreen when screen is not primary

        if (primary) {
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setFullScreen(true);
        } else {
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
//            stage.setFullScreen(true);
            stage.toFront();
        }
    }
}



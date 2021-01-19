package br.com.tlmacedo.binary.views;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewPrincipal {

    private static Stage stage;

    public void openViewPrincipal() throws IOException {
        setStage(new Stage());
        Parent parent;
        Scene scene;

        parent = FXMLLoader.load(getClass().getResource("/fxml/FxmlBinary_v1.02.fxml"));
        scene = new Scene(parent);

        getStage().setTitle("Binary by Thiago Macedo.");
        getStage().setResizable(true);
        getStage().setScene(scene);

        setupStageLocation(getStage(), 1);
        getStage().show();
    }

    private void setupStageLocation(Stage stage, int screenNumber) {
        ObservableList<Screen> screens = Screen.getScreens();
        Screen screen = screens.size() <= screenNumber ? Screen.getPrimary() : screens.get(screenNumber);

        Rectangle2D bounds = screen.getBounds();
//        boolean primary = screen.equals(Screen.getPrimary());    // WORKAROUND: this doesn't work nice in combination with full screen, so this hack is used to prevent going fullscreen when screen is not primary

        if (screen.equals(Screen.getPrimary())) {
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

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ViewPrincipal.stage = stage;
    }
}

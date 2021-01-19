package br.com.tlmacedo.binary.views;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static br.com.tlmacedo.binary.interfaces.Constants.VERSAO_APP;

public class ViewPrincipal {

    private static Stage stage;
    private Parent parent;
    private Scene scene;

    public void openViewPrincipal() throws IOException {

        setStage(new Stage());

        setParent(FXMLLoader.load(getClass().getResource("/fxml/FxmlBinary_v1.03.fxml")));
        setScene(new Scene(getParent()));

        getStage().setTitle(String.format("Binary by Thiago Macedo. %s", VERSAO_APP));
        getStage().setResizable(true);
        getStage().setScene(getScene());

        setupStageLocation(getStage(), 1);
        getStage().show();

    }

    private void setupStageLocation(Stage stage, int screenNumber) {

        List<Screen> screens = Screen.getScreens();
        Screen screen = screens.size() <= screenNumber ? Screen.getPrimary() : screens.get(screenNumber);

        Rectangle2D bounds = screen.getBounds();

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

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}

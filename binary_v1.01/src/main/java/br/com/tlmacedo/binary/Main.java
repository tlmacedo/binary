package br.com.tlmacedo.binary;

import br.com.tlmacedo.binary.view.ViewPrincipal;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new ViewPrincipal().openViewPrincipal();
    }
}



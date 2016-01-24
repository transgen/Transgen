package com.transgen.forms;

import java.io.IOException;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader f = new FXMLLoader((getClass().getResource("/main.fxml")));
        final Parent fxmlRoot = f.load();
        Scene scene = new Scene(fxmlRoot);

        MainApplicationController mac = f.getController();
        mac.setStage(stage);
        mac.setHostServices(HostServicesFactory.getInstance(this));

        scene.getStylesheets().add("/main.css");
        stage.setScene(scene);
        stage.setTitle("TransGen™");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/transgen-icon.png")));
        stage.show();
    }
}
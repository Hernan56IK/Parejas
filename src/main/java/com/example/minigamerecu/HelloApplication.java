package com.example.minigamerecu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación del juego de memoria.
 * Inicializa la aplicación JavaFX y carga la pantalla de inicio.
 */
public class HelloApplication extends Application {
    
    /**
     * Método principal que inicia la aplicación JavaFX.
     * 
     * @param stage El escenario principal de la aplicación
     * @throws IOException Si hay un error al cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        stage.setTitle("Memory Game - Inicio");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * 
     * @param args Argumentos de la línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        launch();
    }
}
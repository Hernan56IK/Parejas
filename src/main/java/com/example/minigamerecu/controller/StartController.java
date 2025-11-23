package com.example.minigamerecu.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {

    @FXML
    private Button startButton;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Rectangle overlay;

    @FXML
    public void initialize() {
        // Asegurar que la imagen de fondo se ajuste al tamaño de la ventana
        if (backgroundImageView != null) {
            Scene scene = backgroundImageView.getScene();
            if (scene != null) {
                backgroundImageView.fitWidthProperty().bind(scene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(scene.heightProperty());
                if (overlay != null) {
                    overlay.widthProperty().bind(scene.widthProperty());
                    overlay.heightProperty().bind(scene.heightProperty());
                }
            } else {
                backgroundImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                        backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());
                        if (overlay != null) {
                            overlay.widthProperty().bind(newScene.widthProperty());
                            overlay.heightProperty().bind(newScene.heightProperty());
                        }
                    }
                });
            }
        }
    }

    @FXML
    private void onStartButtonClick() throws IOException {
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/minigamerecu/view/game.fxml"));
        Scene scene = new Scene(loader.load(), 459, 644);
        stage.setScene(scene);
        stage.setTitle("Memory Game - Juego de Parejas");
        stage.setResizable(true);
        stage.setMinWidth(409);
        stage.setMinHeight(564);
        stage.centerOnScreen();
    }

    @FXML
    private void onButtonHover(MouseEvent event) {
        startButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(218, 165, 32, 0.9); -fx-border-width: 2; -fx-border-radius: 25;");
    }

    @FXML
    private void onButtonExit(MouseEvent event) {
        startButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(184, 134, 11, 0.8); -fx-border-width: 2; -fx-border-radius: 25;");
    }
}


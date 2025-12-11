package com.example.minigamerecu.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.example.minigamerecu.manager.GameManager;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio del juego.
 * Gestiona la selección de dificultad y la navegación a la pantalla de juego.
 */
public class StartController {

    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button expertButton;

    @FXML
    private Label difficultyInfoLabel;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private Rectangle overlay;

    private GameManager.Difficulty selectedDifficulty = GameManager.Difficulty.MEDIUM;
    private final GameManager gameManager = GameManager.getInstance();

    /**
     * Inicializa el controlador y configura los componentes de la interfaz.
     * Ajusta el tamaño de la imagen de fondo y establece la dificultad por defecto.
     */
    @FXML
    public void initialize() {
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
        
        gameManager.setDifficulty(selectedDifficulty);
        updateDifficultyInfo();
        highlightSelectedDifficulty();
    }

    /**
     * Maneja el evento de clic en el botón de inicio.
     * Establece la dificultad seleccionada, calcula el tamaño de ventana y navega a la pantalla de juego.
     * 
     * @throws IOException Si hay un error al cargar el archivo FXML del juego
     */
    @FXML
    private void onStartButtonClick() throws IOException {
        gameManager.setDifficulty(selectedDifficulty);
        
        int gridSize = selectedDifficulty.getGridSize();
        int cardSize = gridSize == 4 ? 80 : 60;
        int gap = 12;
        int padding = 56;
        int headerHeight = 200;
        int footerHeight = 50;
        
        int boardWidth = (cardSize + gap) * gridSize - gap + padding;
        int boardHeight = (cardSize + gap) * gridSize - gap + headerHeight + footerHeight;
        
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/minigamerecu/view/game.fxml"));
        Scene scene = new Scene(loader.load(), boardWidth, boardHeight);
        stage.setScene(scene);
        stage.setTitle("Memory Game - " + selectedDifficulty.getDisplayName());
        stage.setResizable(true);
        stage.setMinWidth(boardWidth - 50);
        stage.setMinHeight(boardHeight - 80);
        stage.centerOnScreen();
    }

    /**
     * Maneja la selección de una dificultad.
     * Actualiza la dificultad seleccionada y la información mostrada en la interfaz.
     * 
     * @param event El evento de acción del botón de dificultad
     */
    @FXML
    private void onDifficultySelected(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        
        if (clickedButton == easyButton) {
            selectedDifficulty = GameManager.Difficulty.EASY;
        } else if (clickedButton == mediumButton) {
            selectedDifficulty = GameManager.Difficulty.MEDIUM;
        } else if (clickedButton == hardButton) {
            selectedDifficulty = GameManager.Difficulty.HARD;
        } else if (clickedButton == expertButton) {
            selectedDifficulty = GameManager.Difficulty.EXPERT;
        }
        
        gameManager.setDifficulty(selectedDifficulty);
        updateDifficultyInfo();
        highlightSelectedDifficulty();
    }

    /**
     * Actualiza la etiqueta de información de dificultad con los detalles de la dificultad seleccionada.
     */
    private void updateDifficultyInfo() {
        if (difficultyInfoLabel != null) {
            String info = String.format("%s: %dx%d, %d pares, %d movimientos",
                selectedDifficulty.getDisplayName(),
                selectedDifficulty.getGridSize(),
                selectedDifficulty.getGridSize(),
                selectedDifficulty.getTotalPairs(),
                selectedDifficulty.getMaxMoves());
            difficultyInfoLabel.setText(info);
        }
    }

    /**
     * Resalta visualmente el botón de dificultad seleccionado.
     */
    private void highlightSelectedDifficulty() {
        resetDifficultyButtonStyle(easyButton, "#4CAF50");
        resetDifficultyButtonStyle(mediumButton, "#FF9800");
        resetDifficultyButtonStyle(hardButton, "#F44336");
        resetDifficultyButtonStyle(expertButton, "#9C27B0");
        
        Button selectedButton = null;
        String selectedColor = "";
        
        switch (selectedDifficulty) {
            case EASY:
                selectedButton = easyButton;
                selectedColor = "#4CAF50";
                break;
            case MEDIUM:
                selectedButton = mediumButton;
                selectedColor = "#FF9800";
                break;
            case HARD:
                selectedButton = hardButton;
                selectedColor = "#F44336";
                break;
            case EXPERT:
                selectedButton = expertButton;
                selectedColor = "#9C27B0";
                break;
        }
        
        if (selectedButton != null) {
            selectedButton.setStyle("-fx-background-color: " + selectedColor + "; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-pref-width: 80; -fx-pref-height: 40; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(255, 215, 0, 0.9); -fx-border-width: 3; -fx-border-radius: 15;");
        }
    }

    /**
     * Restablece el estilo de un botón de dificultad a su estado normal.
     * 
     * @param button El botón a restablecer
     * @param color El color base del botón
     */
    private void resetDifficultyButtonStyle(Button button, String color) {
        if (button != null) {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14; -fx-pref-width: 80; -fx-pref-height: 40; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 0);");
        }
    }

    /**
     * Maneja el evento de hover sobre un botón de dificultad.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onDifficultyButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button != null && !button.getStyle().contains("font-weight: bold")) {
            String color = getButtonColor(button);
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14; -fx-pref-width: 80; -fx-pref-height: 40; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 0);");
        }
    }

    /**
     * Maneja el evento de salida del mouse de un botón de dificultad.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onDifficultyButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button != null && !button.getStyle().contains("font-weight: bold")) {
            String color = getButtonColor(button);
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14; -fx-pref-width: 80; -fx-pref-height: 40; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 0);");
        }
    }

    /**
     * Obtiene el color asociado a un botón de dificultad específico.
     * 
     * @param button El botón del cual obtener el color
     * @return El color hexadecimal del botón
     */
    private String getButtonColor(Button button) {
        if (button == easyButton) return "#4CAF50";
        if (button == mediumButton) return "#FF9800";
        if (button == hardButton) return "#F44336";
        if (button == expertButton) return "#9C27B0";
        return "#8B4513";
    }

    /**
     * Maneja el evento de hover sobre el botón de inicio.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onButtonHover(MouseEvent event) {
        startButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(218, 165, 32, 0.9); -fx-border-width: 2; -fx-border-radius: 25;");
    }

    /**
     * Maneja el evento de salida del mouse del botón de inicio.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onButtonExit(MouseEvent event) {
        startButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(184, 134, 11, 0.8); -fx-border-width: 2; -fx-border-radius: 25;");
    }

    /**
     * Maneja el evento de clic en el botón de salida.
     * Cierra la aplicación.
     */
    @FXML
    private void onExitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Maneja el evento de hover sobre el botón de salida.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onExitButtonHover(MouseEvent event) {
        exitButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(218, 165, 32, 0.9); -fx-border-width: 2; -fx-border-radius: 25;");
    }

    /**
     * Maneja el evento de salida del mouse del botón de salida.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onExitButtonExit(MouseEvent event) {
        exitButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-size: 18; -fx-pref-width: 220; -fx-pref-height: 55; -fx-background-radius: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0); -fx-border-color: rgba(184, 134, 11, 0.8); -fx-border-width: 2; -fx-border-radius: 25;");
    }
}


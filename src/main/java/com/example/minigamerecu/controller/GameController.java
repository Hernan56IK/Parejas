package com.example.minigamerecu.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.minigamerecu.model.Card;

import java.io.IOException;
import java.util.*;

public class GameController {

    private static final String[] SYMBOLS = {"🎮", "🎨", "🎭", "🎪", "🎯", "🎲", "🎸", "🎹"};
    private static final int GRID_SIZE = 4;
    private static final int TOTAL_PAIRS = 8;

    @FXML
    private GridPane board;

    @FXML
    private Label movesLabel;

    @FXML
    private Label pairsLabel;

    @FXML
    private VBox victoryContainer;

    @FXML
    private Label victoryMessage;

    @FXML
    private Label victorySubMessage;

    @FXML
    private Label victoryEmoji1;

    @FXML
    private Label victoryEmoji2;

    @FXML
    private Pane starsContainer;

    @FXML
    private Pane particlesContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Button instructionsButton;

    @FXML
    private ImageView backgroundImage;

    private List<Card> cards = new ArrayList<>();
    private Map<Button, Card> cardMap = new HashMap<>();
    private Map<Button, Integer> buttonIndexMap = new HashMap<>();
    private Map<Button, Label> buttonLabelMap = new HashMap<>();
    private List<Button> flippedButtons = new ArrayList<>();
    private Set<Integer> matchedIndices = new HashSet<>();
    private List<Circle> stars = new ArrayList<>();

    private boolean processing = false;
    private int moves = 0;
    private int matchedPairsCount = 0;
    private boolean gameWon = false;

    @FXML
    public void initialize() {
        setupBackgroundImage();
        createStars();
        animateTitle();
        initializeGame();
    }

    private void setupBackgroundImage() {
        if (backgroundImage != null) {
            // Hacer que la imagen se ajuste al tamaño de la ventana
            Scene scene = backgroundImage.getScene();
            if (scene != null) {
                backgroundImage.fitWidthProperty().bind(scene.widthProperty());
                backgroundImage.fitHeightProperty().bind(scene.heightProperty());
            } else {
                // Si la escena aún no está disponible, esperar a que esté
                backgroundImage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                        backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                    }
                });
            }
        }
    }

    private void createStars() {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Circle star = new Circle(random.nextDouble() * 2.1 + 0.7);
            star.setFill(Color.WHITE);
            star.setOpacity(0.8);
            
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 800;
            star.setLayoutX(x);
            star.setLayoutY(y);
            
            starsContainer.getChildren().add(star);
            stars.add(star);
            
            // Animación de parpadeo
            FadeTransition ft = new FadeTransition(Duration.seconds(3), star);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setDelay(Duration.seconds(random.nextDouble() * 3));
            ft.play();
        }
    }

    private void animateTitle() {
        if (titleLabel != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(3000), titleLabel);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.play();
        }
    }

    private void initializeGame() {
        // Limpiar estado anterior
        board.getChildren().clear();
        cards.clear();
        cardMap.clear();
        buttonIndexMap.clear();
        buttonLabelMap.clear();
        flippedButtons.clear();
        matchedIndices.clear();
        moves = 0;
        matchedPairsCount = 0;
        processing = false;
        gameWon = false;
        victoryContainer.setVisible(false);
        victoryContainer.setManaged(false);
        particlesContainer.getChildren().clear();
        
        updateLabels();

        // Crear valores con símbolos duplicados y sus IDs
        List<Map.Entry<String, Integer>> values = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length; i++) {
            String symbol = SYMBOLS[i];
            values.add(new AbstractMap.SimpleEntry<>(symbol, i));
            values.add(new AbstractMap.SimpleEntry<>(symbol, i));
        }

        // Mezclar
        Collections.shuffle(values);

        // Crear cartas con el ID correcto basado en el símbolo
        for (Map.Entry<String, Integer> entry : values) {
            cards.add(new Card(entry.getValue(), entry.getKey()));
        }

        drawBoard();
    }

    private void drawBoard() {
        int index = 0;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Card card = cards.get(index);
                final int cardIndex = index;

                Button btn = new Button();
                btn.setPrefSize(80, 80);
                btn.setMinSize(80, 80);
                btn.setMaxSize(80, 80);
                btn.setStyle(getCardStyle(cardIndex, false, false));
                
                // Usar Label para mostrar el emoji/texto
                Label contentLabel = new Label("?");
                contentLabel.setFont(new Font("Segoe UI Emoji", 36));
                contentLabel.setStyle("-fx-text-fill: #654321; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
                contentLabel.setMaxWidth(Double.MAX_VALUE);
                contentLabel.setMaxHeight(Double.MAX_VALUE);
                btn.setGraphic(contentLabel);
                btn.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
                btn.setGraphicTextGap(0);
                
                // Ajustar tamaño del botón
                btn.setPrefSize(80, 80);
                btn.setMinSize(80, 80);
                btn.setMaxSize(80, 80);
                
                btn.setOnMouseEntered(e -> {
                    if (!matchedIndices.contains(cardIndex) && !flippedButtons.contains(btn) && !processing) {
                        btn.setStyle(getCardStyle(cardIndex, false, false, true));
                    }
                });
                
                btn.setOnMouseExited(e -> {
                    if (!matchedIndices.contains(cardIndex) && !flippedButtons.contains(btn) && !processing) {
                        btn.setStyle(getCardStyle(cardIndex, false, false, false));
                    }
                });
                
                btn.setOnAction(e -> handleCardClick(btn, cardIndex));

                board.add(btn, col, row);
                cardMap.put(btn, card);
                buttonIndexMap.put(btn, cardIndex);
                buttonLabelMap.put(btn, contentLabel);

                index++;
            }
        }
    }

    private void handleCardClick(Button btn, int index) {
        if (processing || 
            flippedButtons.contains(btn) || 
            matchedIndices.contains(index) ||
            flippedButtons.size() >= 2 ||
            gameWon) {
            return;
        }

        flippedButtons.add(btn);
        Card card = cardMap.get(btn);
        card.setFlipped(true);

        // Animación de bounce-in al voltear
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();

        // Mostrar el símbolo
        Label contentLabel = buttonLabelMap.get(btn);
        if (contentLabel != null) {
            contentLabel.setText(card.getSymbol());
            contentLabel.setFont(new Font("Segoe UI Emoji", 36));
            contentLabel.setStyle("-fx-text-fill: #654321; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
        }
        btn.setStyle(getCardStyle(index, true, false, false));

        if (flippedButtons.size() == 2) {
            moves++;
            updateLabels();
            processing = true;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        checkMatch();
                    });
                }
            }, 500);
        }
    }

    private void checkMatch() {
        if (flippedButtons.size() != 2) {
            processing = false;
            return;
        }

        Button firstBtn = flippedButtons.get(0);
        Button secondBtn = flippedButtons.get(1);
        
        Card firstCard = cardMap.get(firstBtn);
        Card secondCard = cardMap.get(secondBtn);

        int firstIndex = buttonIndexMap.get(firstBtn);
        int secondIndex = buttonIndexMap.get(secondBtn);

        // Comparar tanto por ID como por símbolo para mayor seguridad
        if (firstCard.getId() == secondCard.getId() && 
            firstCard.getSymbol().equals(secondCard.getSymbol())) {
            // Son pares
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchedIndices.add(firstIndex);
            matchedIndices.add(secondIndex);
            matchedPairsCount++;
            updateLabels();

            // Animación de match encontrado
            animateMatchFound(firstBtn);
            animateMatchFound(secondBtn);

            // Cambiar estilo a verde
            firstBtn.setStyle(getCardStyle(firstIndex, true, true, false));
            secondBtn.setStyle(getCardStyle(secondIndex, true, true, false));
            
            // Actualizar color del texto en los labels
            Label label1 = buttonLabelMap.get(firstBtn);
            Label label2 = buttonLabelMap.get(secondBtn);
            if (label1 != null) {
                label1.setStyle("-fx-text-fill: #654321; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
            }
            if (label2 != null) {
                label2.setStyle("-fx-text-fill: #654321; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
            }

            flippedButtons.clear();
            processing = false;

            // Verificar si ganó
            if (matchedPairsCount == TOTAL_PAIRS) {
                gameWon = true;
                createParticles();
                showVictoryMessage();
                // Mostrar alerta después de un breve delay
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> showWinAlert());
                    }
                }, 2000);
            }
        } else {
            // No son pares → voltearlas
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        resetButton(firstBtn, firstIndex);
                        resetButton(secondBtn, secondIndex);
                        
                        firstCard.setFlipped(false);
                        secondCard.setFlipped(false);
                        
                        flippedButtons.clear();
                        processing = false;
                    });
                }
            }, 1000);
        }
    }

    private void animateMatchFound(Button btn) {
        RotateTransition rt = new RotateTransition(Duration.millis(600), btn);
        rt.setFromAngle(0);
        rt.setToAngle(360);
        
        ScaleTransition st = new ScaleTransition(Duration.millis(600), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        
        ParallelTransition pt = new ParallelTransition(rt, st);
        pt.play();
    }

    private void resetButton(Button btn, int index) {
        Label contentLabel = buttonLabelMap.get(btn);
        if (contentLabel != null) {
            contentLabel.setText("?");
            contentLabel.setFont(new Font("Segoe UI Emoji", 36));
            contentLabel.setStyle("-fx-text-fill: #654321; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
        }
        btn.setStyle(getCardStyle(index, false, false, false));
    }

    private String getCardStyle(int index, boolean flipped, boolean matched, boolean hover) {
        if (matched) {
            return "-fx-background-color: linear-gradient(to bottom right, #DAA520, #B8860B); " +
                   "-fx-background-radius: 14; " +
                   "-fx-border-color: #8B4513; " +
                   "-fx-border-width: 3; " +
                   "-fx-border-radius: 14; " +
                   "-fx-text-fill: #654321; " +
                   "-fx-alignment: center; " +
                   "-fx-content-display: center; " +
                   "-fx-effect: dropshadow(gaussian, rgba(139, 69, 19, 0.6), 14, 0, 0, 0);";
        } else if (flipped) {
            return "-fx-background-color: linear-gradient(to bottom right, #F5DEB3, #DEB887); " +
                   "-fx-background-radius: 14; " +
                   "-fx-border-color: #CD853F; " +
                   "-fx-border-width: 3; " +
                   "-fx-border-radius: 14; " +
                   "-fx-text-fill: #654321; " +
                   "-fx-alignment: center; " +
                   "-fx-content-display: center; " +
                   "-fx-effect: dropshadow(gaussian, rgba(139, 69, 19, 0.5), 11, 0, 0, 0);";
        } else {
            String baseStyle = "-fx-background-color: linear-gradient(to bottom right, #FFF8DC, #F5E6D3); " +
                              "-fx-background-radius: 14; " +
                              "-fx-border-color: #8B4513; " +
                              "-fx-border-width: 2; " +
                              "-fx-border-radius: 14; " +
                              "-fx-text-fill: #654321; " +
                              "-fx-alignment: center; " +
                              "-fx-content-display: center; ";
            if (hover) {
                return baseStyle + "-fx-effect: dropshadow(gaussian, rgba(139, 69, 19, 0.6), 14, 0, 0, 0);";
            } else {
                return baseStyle + "-fx-effect: dropshadow(gaussian, rgba(139, 69, 19, 0.4), 7, 0, 0, 0);";
            }
        }
    }

    private String getCardStyle(int index, boolean flipped, boolean matched) {
        return getCardStyle(index, flipped, matched, false);
    }

    private void updateLabels() {
        movesLabel.setText(String.valueOf(moves));
        pairsLabel.setText(matchedPairsCount + "/" + TOTAL_PAIRS);
    }

    private void createParticles() {
        Random random = new Random();
        // Usar el tamaño de la escena para las partículas
        Scene scene = particlesContainer.getScene();
        double width = scene != null ? scene.getWidth() : 459;
        double height = scene != null ? scene.getHeight() : 644;
        
        for (int i = 0; i < 30; i++) {
            Text particle = new Text("✨");
            particle.setFont(new Font(14));
            particle.setFill(Color.YELLOW);
            
            double x = random.nextDouble() * width;
            double y = random.nextDouble() * height;
            particle.setLayoutX(x);
            particle.setLayoutY(y);
            
            particlesContainer.getChildren().add(particle);
            
            // Animación de partícula flotando hacia arriba
            TranslateTransition tt = new TranslateTransition(Duration.seconds(3), particle);
            tt.setFromY(0);
            tt.setToY(-900);
            
            ScaleTransition st = new ScaleTransition(Duration.seconds(3), particle);
            st.setFromX(0);
            st.setFromY(0);
            st.setToX(1.5);
            st.setToY(1.5);
            
            FadeTransition ft = new FadeTransition(Duration.seconds(3), particle);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            
            ParallelTransition pt = new ParallelTransition(tt, st, ft);
            pt.setDelay(Duration.millis(random.nextInt(2000)));
            pt.setOnFinished(e -> particlesContainer.getChildren().remove(particle));
            pt.play();
        }
    }

    private void showVictoryMessage() {
        victoryMessage.setText("¡FELICIDADES!");
        victorySubMessage.setText("✨ ¡Completaste el juego en " + moves + " movimientos! ✨");
        victoryContainer.setVisible(true);
        victoryContainer.setManaged(true);
        
        // Animación de escala
        ScaleTransition st = new ScaleTransition(Duration.millis(500), victoryContainer);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
        
        // Animar emojis con wiggle
        if (victoryEmoji1 != null) {
            animateWiggle(victoryEmoji1);
        }
        if (victoryEmoji2 != null) {
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(200), e -> animateWiggle(victoryEmoji2)));
            delay.play();
        }
    }

    private void animateWiggle(Label label) {
        RotateTransition rt = new RotateTransition(Duration.millis(500), label);
        rt.setFromAngle(0);
        rt.setToAngle(-10);
        rt.setAutoReverse(true);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();
    }

    private void showWinAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("¡Felicidades!");
        alert.setHeaderText("¡Has ganado!");
        alert.setContentText("Has encontrado todas las parejas en " + moves + " movimientos.\n\n¿Qué deseas hacer?");
        
        ButtonType restartButton = new ButtonType("Reiniciar");
        ButtonType menuButton = new ButtonType("Menú Principal");
        
        alert.getButtonTypes().setAll(restartButton, menuButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == restartButton) {
                restartGame();
            } else if (result.get() == menuButton) {
                goToStartMenu();
            }
        }
    }

    private void restartGame() {
        initializeGame();
    }

    private void goToStartMenu() {
        try {
            Stage stage = (Stage) board.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/minigamerecu/view/start.fxml"));
            Scene scene = new Scene(loader.load(), 400, 500);
            stage.setScene(scene);
            stage.setTitle("Memory Game - Inicio");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instrucciones del Juego");
        alert.setHeaderText("🎯 Cómo Jugar");
        alert.setContentText(
            "OBJETIVO:\n" +
            "Encuentra todas las parejas de cartas iguales.\n\n" +
            "CÓMO JUGAR:\n" +
            "1. Haz clic en una carta para voltearla.\n" +
            "2. Haz clic en otra carta para encontrar su pareja.\n" +
            "3. Si las cartas coinciden, se quedarán visibles.\n" +
            "4. Si no coinciden, se voltearán de nuevo.\n" +
            "5. Encuentra todas las parejas para ganar.\n\n" +
            "CONSEJOS:\n" +
            "• Memoriza la posición de las cartas.\n" +
            "• Tienes 30 movimientos para completar el juego.\n" +
            "• ¡Buena suerte!"
        );
        alert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: black;"
        );
        alert.showAndWait();
    }

    @FXML
    private void onInstructionsButtonHover(javafx.scene.input.MouseEvent event) {
        instructionsButton.setStyle("-fx-background-color: rgba(160, 82, 45, 0.95); -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-pref-width: 45; -fx-pref-height: 45; -fx-background-radius: 22; -fx-border-color: rgba(255, 215, 0, 0.9); -fx-border-width: 2; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0);");
    }

    @FXML
    private void onInstructionsButtonExit(javafx.scene.input.MouseEvent event) {
        instructionsButton.setStyle("-fx-background-color: rgba(139, 69, 19, 0.85); -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-pref-width: 45; -fx-pref-height: 45; -fx-background-radius: 22; -fx-border-color: rgba(255, 215, 0, 0.8); -fx-border-width: 2; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");
    }
}

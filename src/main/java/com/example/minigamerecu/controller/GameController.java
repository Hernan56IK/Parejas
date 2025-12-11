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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.minigamerecu.model.Card;
import com.example.minigamerecu.manager.GameManager;

import java.io.IOException;
import java.util.*;

/**
 * Controlador principal del juego de memoria.
 * Gestiona la interfaz de usuario, las interacciones con las cartas, animaciones,
 * sistema de pistas y la lógica del juego.
 */
public class GameController {

    private final GameManager gameManager = GameManager.getInstance();

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
    private Button hintButton;

    @FXML
    private Label hintLabel;

    @FXML
    private ImageView backgroundImage;

    private List<Card> cards = new ArrayList<>();
    private Map<Button, Card> cardMap = new HashMap<>();
    private Map<Button, Integer> buttonIndexMap = new HashMap<>();
    private Map<Button, Text> buttonTextMap = new HashMap<>();
    private Map<Button, javafx.scene.layout.StackPane> buttonPaneMap = new HashMap<>();
    private List<Button> flippedButtons = new ArrayList<>();
    private Set<Integer> matchedIndices = new HashSet<>();
    private List<Circle> stars = new ArrayList<>();

    /**
     * Calcula el tamaño de las cartas según la dificultad actual.
     * 
     * @return El tamaño en píxeles de las cartas (80 para tablero 4x4, 60 para 6x6)
     */
    private int getCardSize() {
        int gridSize = gameManager.getGridSize();
        return gridSize == 4 ? 80 : 60;
    }
    
    /**
     * Calcula el tamaño de fuente para los emojis según la dificultad actual.
     * 
     * @return El tamaño de fuente en píxeles (36 para tablero 4x4, 28 para 6x6)
     */
    private int getFontSize() {
        int gridSize = gameManager.getGridSize();
        return gridSize == 4 ? 36 : 28;
    }
    
    /**
     * Obtiene una fuente compatible con emojis, probando múltiples fuentes disponibles.
     * 
     * @param size El tamaño de la fuente
     * @return Una fuente compatible con emojis o la fuente del sistema por defecto
     */
    private Font getEmojiFont(double size) {
        String[] emojiFonts = {
            "Segoe UI Emoji",
            "Apple Color Emoji",
            "Noto Color Emoji",
            "EmojiOne Color",
            "System"
        };
        
        for (String fontName : emojiFonts) {
            try {
                Font font = new Font(fontName, size);
                if (font != null) {
                    return font;
                }
            } catch (Exception e) {
            }
        }
        
        return new Font(size);
    }

    /**
     * Inicializa el controlador y configura todos los componentes de la interfaz.
     * Se ejecuta automáticamente cuando se carga el FXML.
     */
    @FXML
    public void initialize() {
        setupBackgroundImage();
        createStars();
        animateTitle();
        initializeGame();
        updateHintButton();
    }

    /**
     * Configura la imagen de fondo para que se ajuste al tamaño de la ventana.
     */
    private void setupBackgroundImage() {
        if (backgroundImage != null) {
            Scene scene = backgroundImage.getScene();
            if (scene != null) {
                backgroundImage.fitWidthProperty().bind(scene.widthProperty());
                backgroundImage.fitHeightProperty().bind(scene.heightProperty());
            } else {
                backgroundImage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                        backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                    }
                });
            }
        }
    }

    /**
     * Crea y anima las estrellas decorativas en el fondo.
     */
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
            
            FadeTransition ft = new FadeTransition(Duration.seconds(3), star);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setAutoReverse(true);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setDelay(Duration.seconds(random.nextDouble() * 3));
            ft.play();
        }
    }

    /**
     * Aplica una animación de parpadeo al título del juego.
     */
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

    /**
     * Inicializa una nueva partida del juego.
     * Genera las cartas, las baraja y dibuja el tablero.
     * 
     * @throws IllegalStateException Si no hay suficientes símbolos para el número de pares requeridos
     */
    private void initializeGame() {
        gameManager.startNewGame();
        
        board.getChildren().clear();
        cards.clear();
        cardMap.clear();
        buttonIndexMap.clear();
        buttonTextMap.clear();
        buttonPaneMap.clear();
        flippedButtons.clear();
        matchedIndices.clear();
        victoryContainer.setVisible(false);
        victoryContainer.setManaged(false);
        particlesContainer.getChildren().clear();
        
        updateLabels();
        updateHintButton();

        String[] ALL_SYMBOLS = GameManager.getSymbols();
        int totalPairs = gameManager.getTotalPairs();
        
        if (totalPairs > ALL_SYMBOLS.length) {
            throw new IllegalStateException("No hay suficientes símbolos para " + totalPairs + " pares. Se necesitan al menos " + totalPairs + " símbolos únicos.");
        }
        
        String[] SYMBOLS = new String[totalPairs];
        for (int i = 0; i < totalPairs; i++) {
            String symbol = ALL_SYMBOLS[i];
            if (symbol == null || symbol.trim().isEmpty()) {
                symbol = "❓";
            }
            symbol = symbol.replaceAll("[\\uFE00-\\uFE0F]", "").trim();
            SYMBOLS[i] = symbol;
        }
        
        List<Map.Entry<String, Integer>> values = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length; i++) {
            String symbol = SYMBOLS[i];
            values.add(new AbstractMap.SimpleEntry<>(symbol, i));
            values.add(new AbstractMap.SimpleEntry<>(symbol, i));
        }

        Collections.shuffle(values);

        for (Map.Entry<String, Integer> entry : values) {
            cards.add(new Card(entry.getValue(), entry.getKey()));
        }
        
        gameManager.initializeCards(cards);

        drawBoard();
    }

    /**
     * Dibuja el tablero de juego con todas las cartas.
     * Crea los botones de las cartas y configura sus eventos.
     */
    private void drawBoard() {
        int index = 0;
        int GRID_SIZE = gameManager.getGridSize();
        
        int cardSize = getCardSize();
        int fontSize = getFontSize();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Card card = cards.get(index);
                final int cardIndex = index;

                Button btn = new Button();
                btn.setPrefSize(cardSize, cardSize);
                btn.setMinSize(cardSize, cardSize);
                btn.setMaxSize(cardSize, cardSize);
                btn.setStyle(getCardStyle(cardIndex, false, false));
                
                Text contentText = new Text("?");
                Font emojiFont = getEmojiFont(fontSize);
                contentText.setFont(emojiFont);
                contentText.setFill(javafx.scene.paint.Color.web("#654321"));
                contentText.setTextAlignment(TextAlignment.CENTER);
                
                javafx.scene.layout.StackPane textPane = new javafx.scene.layout.StackPane();
                textPane.setPrefSize(cardSize, cardSize);
                textPane.getChildren().add(contentText);
                textPane.setAlignment(javafx.geometry.Pos.CENTER);
                
                btn.setGraphic(textPane);
                btn.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
                btn.setGraphicTextGap(0);
                
                btn.setPrefSize(cardSize, cardSize);
                btn.setMinSize(cardSize, cardSize);
                btn.setMaxSize(cardSize, cardSize);
                
                btn.setOnMouseEntered(e -> {
                    if (!matchedIndices.contains(cardIndex) && !flippedButtons.contains(btn) && !gameManager.isProcessing()) {
                        btn.setStyle(getCardStyle(cardIndex, false, false, true));
                    }
                });
                
                btn.setOnMouseExited(e -> {
                    if (!matchedIndices.contains(cardIndex) && !flippedButtons.contains(btn) && !gameManager.isProcessing()) {
                        btn.setStyle(getCardStyle(cardIndex, false, false, false));
                    }
                });
                
                btn.setOnAction(e -> handleCardClick(btn, cardIndex));

                board.add(btn, col, row);
                cardMap.put(btn, card);
                buttonIndexMap.put(btn, cardIndex);
                buttonTextMap.put(btn, contentText);
                buttonPaneMap.put(btn, textPane);

                index++;
            }
        }
    }

    /**
     * Maneja el evento de clic en una carta.
     * Voltea la carta y verifica si hay coincidencia cuando se han volteado dos cartas.
     * 
     * @param btn El botón de la carta que fue clickeada
     * @param index El índice de la carta en la lista
     */
    private void handleCardClick(Button btn, int index) {
        if (gameManager.isProcessing() || 
            flippedButtons.contains(btn) || 
            matchedIndices.contains(index) ||
            flippedButtons.size() >= 2 ||
            gameManager.isGameWon() ||
            gameManager.isMaxMovesReached()) {
            return;
        }

        flippedButtons.add(btn);
        Card card = cardMap.get(btn);
        card.setFlipped(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();

        Text contentText = buttonTextMap.get(btn);
        if (contentText != null) {
            int fontSize = getFontSize();
            contentText.setText(card.getSymbol());
            contentText.setFont(getEmojiFont(fontSize));
        }
        btn.setStyle(getCardStyle(index, true, false, false));

        if (flippedButtons.size() == 2) {
            gameManager.incrementMoves();
            updateLabels();
            updateHintButton();
            gameManager.setProcessing(true);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        checkMatch();
                        if (gameManager.isMaxMovesReached() && !gameManager.isGameWon()) {
                            Timer loseTimer = new Timer();
                            loseTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> showLoseAlert());
                                }
                            }, 500);
                        }
                    });
                }
            }, 500);
        }
    }

    /**
     * Verifica si las dos cartas volteadas forman una pareja.
     * Si coinciden, las marca como emparejadas. Si no, las voltea de nuevo después de un delay.
     */
    private void checkMatch() {
        if (flippedButtons.size() != 2) {
            gameManager.setProcessing(false);
            return;
        }

        Button firstBtn = flippedButtons.get(0);
        Button secondBtn = flippedButtons.get(1);
        
        Card firstCard = cardMap.get(firstBtn);
        Card secondCard = cardMap.get(secondBtn);

        int firstIndex = buttonIndexMap.get(firstBtn);
        int secondIndex = buttonIndexMap.get(secondBtn);

        if (firstCard.getId() == secondCard.getId() && 
            firstCard.getSymbol().equals(secondCard.getSymbol())) {
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchedIndices.add(firstIndex);
            matchedIndices.add(secondIndex);
            gameManager.incrementMatchedPairs();
            updateLabels();
            updateHintButton();

            animateMatchFound(firstBtn);
            animateMatchFound(secondBtn);

            firstBtn.setStyle(getCardStyle(firstIndex, true, true, false));
            secondBtn.setStyle(getCardStyle(secondIndex, true, true, false));

            flippedButtons.clear();
            gameManager.setProcessing(false);

            if (gameManager.getMatchedPairsCount() == gameManager.getTotalPairs()) {
                gameManager.setGameWon(true);
                createParticles();
                showVictoryMessage();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> showWinAlert());
                    }
                }, 2000);
            }
        } else {
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
                        gameManager.setProcessing(false);
                    });
                }
            }, 1000);
        }
    }

    /**
     * Aplica una animación de rotación y escala cuando se encuentra una pareja.
     * 
     * @param btn El botón de la carta a animar
     */
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

    /**
     * Restablece un botón de carta a su estado inicial (volteada hacia abajo).
     * 
     * @param btn El botón a restablecer
     * @param index El índice de la carta
     */
    private void resetButton(Button btn, int index) {
        Text contentText = buttonTextMap.get(btn);
        if (contentText != null) {
            int fontSize = getFontSize();
            contentText.setText("?");
            contentText.setFont(getEmojiFont(fontSize));
        }
        btn.setStyle(getCardStyle(index, false, false, false));
    }

    /**
     * Genera el estilo CSS para una carta según su estado.
     * 
     * @param index El índice de la carta
     * @param flipped true si la carta está volteada
     * @param matched true si la carta está emparejada
     * @param hover true si el mouse está sobre la carta
     * @return El string con el estilo CSS
     */
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

    /**
     * Genera el estilo CSS para una carta según su estado (sin hover).
     * 
     * @param index El índice de la carta
     * @param flipped true si la carta está volteada
     * @param matched true si la carta está emparejada
     * @return El string con el estilo CSS
     */
    private String getCardStyle(int index, boolean flipped, boolean matched) {
        return getCardStyle(index, flipped, matched, false);
    }

    /**
     * Actualiza las etiquetas de movimientos y parejas encontradas en la interfaz.
     */
    private void updateLabels() {
        movesLabel.setText(String.valueOf(gameManager.getMoves()));
        pairsLabel.setText(gameManager.getMatchedPairsCount() + "/" + gameManager.getTotalPairs());
    }

    /**
     * Crea partículas animadas para celebrar la victoria.
     */
    private void createParticles() {
        Random random = new Random();
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

    /**
     * Muestra el mensaje de victoria con animación.
     */
    private void showVictoryMessage() {
        victoryMessage.setText("¡FELICIDADES!");
        victorySubMessage.setText("✨ ¡Completaste el juego en " + gameManager.getMoves() + " movimientos! ✨");
        victoryContainer.setVisible(true);
        victoryContainer.setManaged(true);
        
        ScaleTransition st = new ScaleTransition(Duration.millis(500), victoryContainer);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
        
        if (victoryEmoji1 != null) {
            animateWiggle(victoryEmoji1);
        }
        if (victoryEmoji2 != null) {
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(200), e -> animateWiggle(victoryEmoji2)));
            delay.play();
        }
    }

    /**
     * Aplica una animación de balanceo a una etiqueta.
     * 
     * @param label La etiqueta a animar
     */
    private void animateWiggle(Label label) {
        RotateTransition rt = new RotateTransition(Duration.millis(500), label);
        rt.setFromAngle(0);
        rt.setToAngle(-10);
        rt.setAutoReverse(true);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();
    }

    /**
     * Muestra un diálogo de alerta cuando el jugador gana el juego.
     * Incluye estadísticas y opciones para reiniciar o volver al menú.
     */
    private void showWinAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("¡Felicidades!");
        alert.setHeaderText("¡Has ganado!");
        
        int moves = gameManager.getMoves();
        int bestScore = gameManager.getBestScore();
        int totalGames = gameManager.getTotalGamesPlayed();
        double winRate = gameManager.getWinRate();
        
        String statsText = "";
        if (totalGames > 1) {
            statsText = String.format(
                "\n📊 Estadísticas:\n" +
                "• Mejor puntuación: %d movimientos\n" +
                "• Partidas jugadas: %d\n" +
                "• Tasa de victoria: %.1f%%\n",
                bestScore, totalGames, winRate
            );
        }
        
        alert.setContentText("Has encontrado todas las parejas en " + moves + " movimientos." + statsText + "\n¿Qué deseas hacer?");
        
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
    
    /**
     * Muestra un diálogo de alerta cuando el jugador pierde el juego.
     * Ofrece opciones para reiniciar o volver al menú.
     */
    private void showLoseAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("¡Se acabaron los movimientos!");
        alert.setHeaderText("Has alcanzado el límite de 20 movimientos");
        alert.setContentText("No lograste encontrar todas las parejas en el tiempo límite.\n\n¿Qué deseas hacer?");
        
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

    /**
     * Reinicia el juego actual.
     */
    private void restartGame() {
        initializeGame();
    }

    /**
     * Navega de vuelta al menú de inicio.
     * 
     * @throws IOException Si hay un error al cargar el archivo FXML del menú
     */
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

    /**
     * Muestra un diálogo con las instrucciones del juego.
     */
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
            "• Tienes " + gameManager.getMaxMoves() + " movimientos para completar el juego.\n" +
            "• Dificultad: " + gameManager.getCurrentDifficulty().getDisplayName() + "\n" +
            "• Tablero: " + gameManager.getGridSize() + "x" + gameManager.getGridSize() + "\n" +
            "• Parejas: " + gameManager.getTotalPairs() + "\n" +
            "• ¡Buena suerte!"
        );
        alert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: black;"
        );
        alert.showAndWait();
    }

    /**
     * Maneja el evento de hover sobre el botón de instrucciones.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onInstructionsButtonHover(javafx.scene.input.MouseEvent event) {
        instructionsButton.setStyle("-fx-background-color: rgba(160, 82, 45, 0.95); -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-pref-width: 45; -fx-pref-height: 45; -fx-background-radius: 22; -fx-border-color: rgba(255, 215, 0, 0.9); -fx-border-width: 2; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0);");
    }

    /**
     * Maneja el evento de salida del mouse del botón de instrucciones.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onInstructionsButtonExit(javafx.scene.input.MouseEvent event) {
        instructionsButton.setStyle("-fx-background-color: rgba(139, 69, 19, 0.85); -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-pref-width: 45; -fx-pref-height: 45; -fx-background-radius: 22; -fx-border-color: rgba(255, 215, 0, 0.8); -fx-border-width: 2; -fx-border-radius: 22; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");
    }

    /**
     * Usa una pista para mostrar temporalmente una pareja de cartas.
     * Bloquea la interacción con las cartas mientras se muestra la pista.
     */
    @FXML
    private void useHint() {
        if (!gameManager.canUseHint() || gameManager.isProcessing()) {
            return;
        }

        List<Integer> unmatchedIndices = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (!matchedIndices.contains(i)) {
                unmatchedIndices.add(i);
            }
        }

        if (unmatchedIndices.size() < 2) {
            return;
        }

        Integer firstIndex = null;
        Integer secondIndex = null;

        for (int i = 0; i < unmatchedIndices.size() && firstIndex == null; i++) {
            int idx1 = unmatchedIndices.get(i);
            Card card1 = cards.get(idx1);
            
            for (int j = i + 1; j < unmatchedIndices.size(); j++) {
                int idx2 = unmatchedIndices.get(j);
                Card card2 = cards.get(idx2);
                
                if (card1.getId() == card2.getId() && 
                    card1.getSymbol().equals(card2.getSymbol())) {
                    firstIndex = idx1;
                    secondIndex = idx2;
                    break;
                }
            }
        }

        if (firstIndex == null || secondIndex == null) {
            return;
        }

        gameManager.useHint();

        Button firstBtn = null;
        Button secondBtn = null;

        for (Map.Entry<Button, Integer> entry : buttonIndexMap.entrySet()) {
            if (entry.getValue().equals(firstIndex)) {
                firstBtn = entry.getKey();
            }
            if (entry.getValue().equals(secondIndex)) {
                secondBtn = entry.getKey();
            }
        }

        if (firstBtn == null || secondBtn == null) {
            return;
        }

        gameManager.setProcessing(true);
        
        if (hintButton != null) {
            hintButton.setMouseTransparent(true);
        }

        showHintCards(firstBtn, firstIndex, secondBtn, secondIndex);
        updateHintButton();
    }

    /**
     * Muestra temporalmente dos cartas como pista con animación.
     * 
     * @param firstBtn El botón de la primera carta de la pista
     * @param firstIndex El índice de la primera carta
     * @param secondBtn El botón de la segunda carta de la pista
     * @param secondIndex El índice de la segunda carta
     */
    private void showHintCards(Button firstBtn, int firstIndex, Button secondBtn, int secondIndex) {
        Card firstCard = cardMap.get(firstBtn);
        Card secondCard = cardMap.get(secondBtn);

        boolean firstWasFlipped = firstCard.isFlipped();
        boolean secondWasFlipped = secondCard.isFlipped();

        for (Button btn : cardMap.keySet()) {
            btn.setMouseTransparent(true);
        }

        firstCard.setFlipped(true);
        secondCard.setFlipped(true);

        Text firstText = buttonTextMap.get(firstBtn);
        Text secondText = buttonTextMap.get(secondBtn);

        if (firstText != null) {
            firstText.setText(firstCard.getSymbol());
        }
        if (secondText != null) {
            secondText.setText(secondCard.getSymbol());
        }

        String hintStyle = "-fx-background-color: linear-gradient(to bottom right, #FFD700, #FFA500); " +
                          "-fx-background-radius: 14; " +
                          "-fx-border-color: #FF6347; " +
                          "-fx-border-width: 4; " +
                          "-fx-border-radius: 14; " +
                          "-fx-text-fill: #654321; " +
                          "-fx-alignment: center; " +
                          "-fx-content-display: center; " +
                          "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.8), 20, 0, 0, 0);";

        firstBtn.setStyle(hintStyle);
        secondBtn.setStyle(hintStyle);

        ScaleTransition st1 = new ScaleTransition(Duration.millis(300), firstBtn);
        st1.setFromX(1.0);
        st1.setFromY(1.0);
        st1.setToX(1.15);
        st1.setToY(1.15);
        st1.setAutoReverse(true);
        st1.setCycleCount(2);

        ScaleTransition st2 = new ScaleTransition(Duration.millis(300), secondBtn);
        st2.setFromX(1.0);
        st2.setFromY(1.0);
        st2.setToX(1.15);
        st2.setToY(1.15);
        st2.setAutoReverse(true);
        st2.setCycleCount(2);

        ParallelTransition pt = new ParallelTransition(st1, st2);
        pt.play();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!firstWasFlipped) {
                        firstCard.setFlipped(false);
                        resetButton(firstBtn, firstIndex);
                    } else {
                        firstBtn.setStyle(getCardStyle(firstIndex, true, false, false));
                    }

                    if (!secondWasFlipped) {
                        secondCard.setFlipped(false);
                        resetButton(secondBtn, secondIndex);
                    } else {
                        secondBtn.setStyle(getCardStyle(secondIndex, true, false, false));
                    }

                    for (Button btn : cardMap.keySet()) {
                        int btnIndex = buttonIndexMap.get(btn);
                        if (!matchedIndices.contains(btnIndex) && 
                            !gameManager.isGameWon() && 
                            !gameManager.isMaxMovesReached()) {
                            btn.setMouseTransparent(false);
                        }
                    }

                    gameManager.setProcessing(false);
                    
                    updateHintButton();
                });
            }
        }, 2000);
    }

    /**
     * Actualiza el estado y apariencia del botón de pistas.
     * Deshabilita el botón si no se pueden usar más pistas.
     */
    private void updateHintButton() {
        if (hintButton == null) {
            return;
        }

        int hintsRemaining = gameManager.getHintsRemaining();
        boolean canUse = gameManager.canUseHint();

        if (hintLabel != null) {
            hintLabel.setText("💡 " + hintsRemaining);
        }

        if (!canUse) {
            hintButton.setDisable(true);
            hintButton.setMouseTransparent(false);
            hintButton.setStyle("-fx-background-color: rgba(100, 100, 100, 0.6); -fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 20; -fx-font-weight: bold; -fx-pref-width: 60; -fx-pref-height: 60; -fx-background-radius: 30; -fx-border-color: rgba(150, 150, 150, 0.5); -fx-border-width: 2; -fx-border-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 0);");
        } else {
            hintButton.setDisable(false);
            hintButton.setMouseTransparent(false);
            hintButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.85); -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-pref-width: 60; -fx-pref-height: 60; -fx-background-radius: 30; -fx-border-color: rgba(255, 215, 0, 0.9); -fx-border-width: 2; -fx-border-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        }
    }

    /**
     * Maneja el evento de hover sobre el botón de pistas.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onHintButtonHover(javafx.scene.input.MouseEvent event) {
        if (gameManager.canUseHint() && !hintButton.isDisable()) {
            hintButton.setStyle("-fx-background-color: rgba(255, 140, 0, 0.95); -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-pref-width: 60; -fx-pref-height: 60; -fx-background-radius: 30; -fx-border-color: rgba(255, 215, 0, 1.0); -fx-border-width: 3; -fx-border-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 12, 0, 0, 0);");
        }
    }

    /**
     * Maneja el evento de salida del mouse del botón de pistas.
     * 
     * @param event El evento de mouse
     */
    @FXML
    private void onHintButtonExit(javafx.scene.input.MouseEvent event) {
        if (gameManager.canUseHint() && !hintButton.isDisable()) {
            hintButton.setStyle("-fx-background-color: rgba(255, 165, 0, 0.85); -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-pref-width: 60; -fx-pref-height: 60; -fx-background-radius: 30; -fx-border-color: rgba(255, 215, 0, 0.9); -fx-border-width: 2; -fx-border-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        }
    }
}

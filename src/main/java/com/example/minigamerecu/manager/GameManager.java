package com.example.minigamerecu.manager;

import com.example.minigamerecu.model.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor principal del juego que implementa el patrón Singleton.
 * Gestiona el estado global del juego, configuración, dificultades y estadísticas.
 * 
 * @author MiniGameRecu
 * @version 1.0
 */
public class GameManager {
    
    private static GameManager instance;
    
    private static final String[] SYMBOLS = {
        "🎮", "🎨", "🎭", "🎪", "🎯", "🎲", "🎸", "🎹",
        "🎺", "🎻", "🎤", "🎧", "🎬", "🎥", "📷", "📹",
        "🎫", "🏆", "🥇", "🥈", "🥉", "⚽", "🏀", "🏈",
        "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸", "🏒",
        "🏑", "🏏", "🥊", "🎣", "🎽", "🎿", "🏂", "🏄",
        "🏊", "🏋", "🚴", "🚵", "🤸", "🤼", "🤽", "🤾",
        "🤹", "🏃", "🚶", "🏇", "⛹", "🤺", "🏌", "🧗"
    };
    
    /**
     * Enum que representa los diferentes niveles de dificultad del juego.
     * Cada dificultad tiene un tamaño de tablero, número de pares y límite de movimientos.
     */
    public enum Difficulty {
        EASY(4, 8, 30, "Fácil"),
        MEDIUM(4, 8, 20, "Medio"),
        HARD(6, 18, 40, "Difícil"),
        EXPERT(6, 18, 30, "Experto");
        
        private final int gridSize;
        private final int totalPairs;
        private final int maxMoves;
        private final String displayName;
        
        /**
         * Constructor del enum Difficulty.
         * 
         * @param gridSize Tamaño del tablero (gridSize x gridSize)
         * @param totalPairs Número total de pares de cartas
         * @param maxMoves Número máximo de movimientos permitidos
         * @param displayName Nombre para mostrar de la dificultad
         */
        Difficulty(int gridSize, int totalPairs, int maxMoves, String displayName) {
            this.gridSize = gridSize;
            this.totalPairs = totalPairs;
            this.maxMoves = maxMoves;
            this.displayName = displayName;
        }
        
        /**
         * Obtiene el tamaño del tablero.
         * 
         * @return El tamaño del tablero (gridSize x gridSize)
         */
        public int getGridSize() {
            return gridSize;
        }
        
        /**
         * Obtiene el número total de pares de cartas.
         * 
         * @return El número total de pares
         */
        public int getTotalPairs() {
            return totalPairs;
        }
        
        /**
         * Obtiene el número máximo de movimientos permitidos.
         * 
         * @return El límite de movimientos
         */
        public int getMaxMoves() {
            return maxMoves;
        }
        
        /**
         * Obtiene el nombre para mostrar de la dificultad.
         * 
         * @return El nombre de la dificultad
         */
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    
    private static final int GRID_SIZE = 4;
    private static final int TOTAL_PAIRS = 8;
    private static final int MAX_MOVES = 20;
    
    private int moves;
    private int matchedPairsCount;
    private boolean gameWon;
    private boolean processing;
    private List<Card> currentCards;
    
    private static final int MAX_HINTS = 3;
    private int hintsUsed;
    
    private int totalGamesPlayed;
    private int totalGamesWon;
    private int bestScore;
    
    private GameManager() {
        resetGame();
        totalGamesPlayed = 0;
        totalGamesWon = 0;
        bestScore = Integer.MAX_VALUE;
    }
    
    /**
     * Obtiene la instancia única del GameManager (patrón Singleton).
     * Implementa doble verificación para garantizar thread-safety.
     * 
     * @return La instancia única de GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Reinicia el estado del juego para una nueva partida.
     * Restablece movimientos, parejas encontradas, estado de victoria y pistas usadas.
     */
    public void resetGame() {
        moves = 0;
        matchedPairsCount = 0;
        gameWon = false;
        processing = false;
        currentCards = new ArrayList<>();
        hintsUsed = 0;
    }
    
    /**
     * Inicializa las cartas para una nueva partida.
     * 
     * @param cards Lista de cartas generadas para el juego
     */
    public void initializeCards(List<Card> cards) {
        this.currentCards = new ArrayList<>(cards);
    }
    
    /**
     * Obtiene el número de movimientos realizados en la partida actual.
     * 
     * @return El número de movimientos
     */
    public int getMoves() {
        return moves;
    }
    
    /**
     * Incrementa el contador de movimientos en uno.
     */
    public void incrementMoves() {
        this.moves++;
    }
    
    /**
     * Obtiene el número de parejas encontradas en la partida actual.
     * 
     * @return El número de parejas encontradas
     */
    public int getMatchedPairsCount() {
        return matchedPairsCount;
    }
    
    /**
     * Incrementa el contador de parejas encontradas en uno.
     */
    public void incrementMatchedPairs() {
        this.matchedPairsCount++;
    }
    
    /**
     * Verifica si el juego ha sido ganado.
     * 
     * @return true si el juego ha sido ganado, false en caso contrario
     */
    public boolean isGameWon() {
        return gameWon;
    }
    
    /**
     * Establece el estado de victoria del juego.
     * Si el juego se gana, actualiza las estadísticas globales.
     * 
     * @param gameWon true si el juego ha sido ganado, false en caso contrario
     */
    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
        if (gameWon) {
            totalGamesWon++;
            if (moves < bestScore) {
                bestScore = moves;
            }
        }
    }
    
    /**
     * Verifica si el juego está procesando una acción (por ejemplo, mostrando una pista).
     * 
     * @return true si el juego está procesando, false en caso contrario
     */
    public boolean isProcessing() {
        return processing;
    }
    
    /**
     * Establece el estado de procesamiento del juego.
     * 
     * @param processing true si el juego está procesando, false en caso contrario
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
    
    /**
     * Obtiene una copia de la lista de cartas actuales.
     * 
     * @return Una nueva lista con las cartas actuales
     */
    public List<Card> getCurrentCards() {
        return new ArrayList<>(currentCards);
    }
    
    /**
     * Obtiene una copia del array de símbolos disponibles.
     * 
     * @return Un array con los símbolos disponibles para las cartas
     */
    public static String[] getSymbols() {
        return SYMBOLS.clone();
    }
    
    /**
     * Obtiene la dificultad actual del juego.
     * 
     * @return La dificultad actual
     */
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    /**
     * Establece la dificultad del juego.
     * 
     * @param difficulty La dificultad a establecer
     */
    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }
    
    /**
     * Obtiene el tamaño del tablero según la dificultad actual.
     * 
     * @return El tamaño del tablero (gridSize x gridSize)
     */
    public int getGridSize() {
        return currentDifficulty.getGridSize();
    }
    
    /**
     * Obtiene el número total de pares según la dificultad actual.
     * 
     * @return El número total de pares
     */
    public int getTotalPairs() {
        return currentDifficulty.getTotalPairs();
    }
    
    /**
     * Obtiene el número máximo de movimientos según la dificultad actual.
     * 
     * @return El límite de movimientos
     */
    public int getMaxMoves() {
        return currentDifficulty.getMaxMoves();
    }
    
    /**
     * Verifica si se ha alcanzado el límite de movimientos.
     * 
     * @return true si se alcanzó el límite, false en caso contrario
     */
    public boolean isMaxMovesReached() {
        return moves >= getMaxMoves();
    }
    
    /**
     * Obtiene el tamaño de tablero por defecto (compatibilidad).
     * 
     * @return El tamaño de tablero por defecto
     */
    public static int getDefaultGridSize() {
        return GRID_SIZE;
    }
    
    /**
     * Obtiene el número de pares por defecto (compatibilidad).
     * 
     * @return El número de pares por defecto
     */
    public static int getDefaultTotalPairs() {
        return TOTAL_PAIRS;
    }
    
    /**
     * Obtiene el límite de movimientos por defecto (compatibilidad).
     * 
     * @return El límite de movimientos por defecto
     */
    public static int getDefaultMaxMoves() {
        return MAX_MOVES;
    }
    
    /**
     * Inicia una nueva partida.
     * Incrementa el contador de partidas jugadas y reinicia el estado del juego.
     */
    public void startNewGame() {
        totalGamesPlayed++;
        resetGame();
    }
    
    /**
     * Obtiene el número total de partidas jugadas.
     * 
     * @return El número total de partidas jugadas
     */
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }
    
    /**
     * Obtiene el número total de partidas ganadas.
     * 
     * @return El número total de partidas ganadas
     */
    public int getTotalGamesWon() {
        return totalGamesWon;
    }
    
    /**
     * Obtiene la mejor puntuación (menor número de movimientos para ganar).
     * 
     * @return La mejor puntuación, o 0 si no hay puntuación registrada
     */
    public int getBestScore() {
        return bestScore == Integer.MAX_VALUE ? 0 : bestScore;
    }
    
    /**
     * Calcula y obtiene la tasa de victoria en porcentaje.
     * 
     * @return La tasa de victoria como porcentaje (0.0 a 100.0)
     */
    public double getWinRate() {
        if (totalGamesPlayed == 0) {
            return 0.0;
        }
        return (double) totalGamesWon / totalGamesPlayed * 100.0;
    }
    
    /**
     * Resetea todas las estadísticas globales del juego.
     * Útil para testing o reinicio completo de estadísticas.
     */
    public void resetStatistics() {
        totalGamesPlayed = 0;
        totalGamesWon = 0;
        bestScore = Integer.MAX_VALUE;
    }
    
    /**
     * Obtiene el número máximo de pistas permitidas por partida.
     * 
     * @return El número máximo de pistas
     */
    public static int getMaxHints() {
        return MAX_HINTS;
    }
    
    /**
     * Obtiene el número de pistas usadas en la partida actual.
     * 
     * @return El número de pistas usadas
     */
    public int getHintsUsed() {
        return hintsUsed;
    }
    
    /**
     * Obtiene el número de pistas restantes en la partida actual.
     * 
     * @return El número de pistas restantes
     */
    public int getHintsRemaining() {
        return MAX_HINTS - hintsUsed;
    }
    
    /**
     * Verifica si se puede usar una pista.
     * 
     * @return true si se puede usar una pista, false en caso contrario
     */
    public boolean canUseHint() {
        return hintsUsed < MAX_HINTS && !gameWon && !isMaxMovesReached();
    }
    
    /**
     * Usa una pista si es posible.
     * Incrementa el contador de pistas usadas si se cumplen las condiciones.
     */
    public void useHint() {
        if (canUseHint()) {
            hintsUsed++;
        }
    }
}


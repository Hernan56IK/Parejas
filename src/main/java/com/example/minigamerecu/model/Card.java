package com.example.minigamerecu.model;

/**
 * Representa una carta del juego de memoria.
 * Cada carta tiene un identificador único, un símbolo (emoji), y estados de emparejamiento y volteo.
 */
public class Card {
    private int id;
    private String symbol;
    private boolean matched;
    private boolean flipped;

    /**
     * Constructor de la clase Card.
     * 
     * @param id Identificador único de la carta (usado para emparejar cartas)
     * @param symbol Símbolo o emoji que representa la carta
     */
    public Card(int id, String symbol) {
        this.id = id;
        this.symbol = symbol;
        this.matched = false;
        this.flipped = false;
    }

    /**
     * Obtiene el identificador único de la carta.
     * 
     * @return El identificador de la carta
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el símbolo (emoji) de la carta.
     * 
     * @return El símbolo de la carta
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Verifica si la carta ha sido emparejada.
     * 
     * @return true si la carta está emparejada, false en caso contrario
     */
    public boolean isMatched() {
        return matched;
    }

    /**
     * Establece el estado de emparejamiento de la carta.
     * 
     * @param matched true si la carta está emparejada, false en caso contrario
     */
    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    /**
     * Verifica si la carta está volteada (visible).
     * 
     * @return true si la carta está volteada, false en caso contrario
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Establece el estado de volteo de la carta.
     * 
     * @param flipped true si la carta está volteada, false en caso contrario
     */
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}


package com.example.minigamerecu.model;

public class Card {
    private int id;        // Número de carta (par)
    private String symbol;  // Emoji/símbolo de la carta
    private boolean matched;
    private boolean flipped;

    public Card(int id, String symbol) {
        this.id = id;
        this.symbol = symbol;
        this.matched = false;
        this.flipped = false;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}


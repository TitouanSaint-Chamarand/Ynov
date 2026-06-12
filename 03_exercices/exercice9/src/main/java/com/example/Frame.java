package com.example;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private int score;
    private final boolean lastFrame;
    private final IGenerateur generateur;
    private final List<Roll> rolls;

    public Frame(IGenerateur generateur, boolean lastFrame) {
        this.lastFrame = lastFrame;
        this.generateur = generateur;
        this.rolls = new ArrayList<>();
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public boolean makeRoll() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

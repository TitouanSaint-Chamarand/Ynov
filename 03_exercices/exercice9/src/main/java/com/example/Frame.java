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
        if (!canRoll()) {
            return false;
        }

        int maxPins = getMaxPinsForNextRoll();
        int pins = generateur.randomPin(maxPins);
        rolls.add(new Roll(pins));
        score += pins;
        return true;
    }

    private boolean canRoll() {
        if (rolls.size() >= getMaxRolls()) {
            return false;
        }

        if (!lastFrame && rolls.size() == 1 && rolls.get(0).getPins() == 10) {
            return false;
        }

        return true;
    }

    private int getMaxRolls() {
        if (!lastFrame) {
            return 2;
        }

        if (rolls.size() < 2) {
            return 3;
        }

        int first = rolls.get(0).getPins();
        int second = rolls.get(1).getPins();

        if (first == 10 || first + second == 10) {
            return 3;
        }

        return 2;
    }

    private int getMaxPinsForNextRoll() {
        if (rolls.isEmpty()) {
            return 10;
        }

        if (!lastFrame) {
            return 10 - rolls.get(0).getPins();
        }

        if (rolls.size() == 1) {
            if (rolls.get(0).getPins() == 10) {
                return 10;
            }
            return 10 - rolls.get(0).getPins();
        }

        int first = rolls.get(0).getPins();
        int second = rolls.get(1).getPins();

        if (first == 10 && second != 10) {
            return 10 - second;
        }

        return 10;
    }
}

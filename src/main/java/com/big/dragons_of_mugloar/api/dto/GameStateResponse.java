package com.big.dragons_of_mugloar.api.dto;

import lombok.Data;

@Data
public class GameStateResponse {
    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
    private int turn;
}

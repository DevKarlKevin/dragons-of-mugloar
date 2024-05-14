package com.big.dragons_of_mugloar.entity;

import com.big.dragons_of_mugloar.api.dto.PurchaseStateResponse;
import com.big.dragons_of_mugloar.api.dto.TaskStateResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class GameState {
    private String gameId;
    private String currentTaskId;
    private int sequentRuns = 1;
    private int lives = 0;
    private int gold = 0;
    private int score;
    private int turn;
    private List<String> ignoreTaskIds = new ArrayList<>();
    private HashSet<String> purchasedItems = new HashSet<>();

    public void addCurrentTaskToIgnoreList() {
        ignoreTaskIds.add(currentTaskId);
    }

    public void increaseIteration() {
        sequentRuns += 1;
    }

    public void refreshIgnoreList() {
        ignoreTaskIds = new ArrayList<>();
    }

    public void addPurchasedItem(String itemId) {
        purchasedItems.add(itemId);
    }

    public boolean itemIsPurchased(String item) {
        return purchasedItems.contains(item);
    }

    public void updateGameStateAfterTaskResponse(TaskStateResponse taskResponse) {
        lives = taskResponse.getLives();
        gold = taskResponse.getGold();
        score = taskResponse.getScore();
        turn = taskResponse.getTurn();
    }

    public void updateGameStateAfterPurchaseResponse(PurchaseStateResponse itemResult) {
        lives = itemResult.getLives();
        gold = itemResult.getGold();
        turn = itemResult.getTurn();
    }
}

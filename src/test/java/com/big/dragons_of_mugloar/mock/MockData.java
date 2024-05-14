package com.big.dragons_of_mugloar.mock;

import com.big.dragons_of_mugloar.api.dto.*;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import com.big.dragons_of_mugloar.enums.TaskProbability;

public class MockData {
    public static final String MOCK_GAME_ID = "GAME_ID";
    public static final String MOCK_AD_ID = "AD_ID";
    private static final int MOCK_INITIAL_LIVES = 3;

    public static GameState createInitialGameState() {
        var gameState = new GameState();
        gameState.setGameId(MOCK_GAME_ID);
        gameState.setTurn(1);
        gameState.setGold(0);
        return gameState;
    }

    public static GameStateResponse mockSuccessStartResponse() {
        var response = new GameStateResponse();
        response.setGameId(MOCK_GAME_ID);
        response.setLives(MOCK_INITIAL_LIVES);
        return response;
    }

    public static PurchaseStateResponse mockPurchaseResponse(GameState gameState, ShopItem shopItem) {
        return PurchaseStateResponse.builder()
                .lives(shopItem.equals(ShopItem.HEALING_POTION) ? gameState.getLives() + 1 : gameState.getLives())
                .turn(gameState.getTurn() + 1)
                .level(1)
                .gold(gameState.getGold() - shopItem.getExpectedCost())
                .shoppingSuccess(true)
                .build();
    }

    public static TaskResponse mockTaskResponse() {
        var taskResponse = new TaskResponse();
        taskResponse.setAdId(MOCK_AD_ID);
        taskResponse.setProbability(TaskProbability.IMPOSSIBLE.getProbability());
        taskResponse.setMessage("Mock message");
        taskResponse.setReward(10);
        return taskResponse;
    }

    public static TaskStateResponse mockTaskStateResponse(GameState gameState, int reward, boolean success) {
        return TaskStateResponse.builder()
                .success(success)
                .lives(success ? gameState.getLives() : 0)
                .turn(gameState.getTurn() + 1)
                .gold(reward)
                .score(reward * 10)
                .build();
    }

    public static ShopItemResponse mockShopItem(ShopItem shopItem) {
        var item = new ShopItemResponse();
        item.setId(shopItem.getId());
        item.setName(shopItem.name());
        item.setCost(shopItem.getExpectedCost());
        return item;
    }
}

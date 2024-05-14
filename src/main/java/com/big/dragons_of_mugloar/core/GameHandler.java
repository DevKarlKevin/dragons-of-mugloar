package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.api.dto.PurchaseStateResponse;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import com.big.dragons_of_mugloar.exception.GameOverException;
import com.big.dragons_of_mugloar.exception.MissingTaskException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.big.dragons_of_mugloar.GameMessages.*;

@Slf4j
@AllArgsConstructor
public class GameHandler {
    private final GameState gameState;
    private final MugloarDataApi dataApi;
    private final TaskHandler taskHandler;
    private final ItemHandler itemHandler;
    private static final int GAME_ITERATION_POINT = 1000;
    private static final List<String> CONTINUE_INPUTS = Arrays.asList("y", "yes");

    public GameHandler() {
        this.gameState = new GameState();
        this.dataApi = new MugloarDataApi();
        this.taskHandler = new TaskHandler(dataApi);
        this.itemHandler = new ItemHandler(dataApi);
    }

    public void startGame() throws InterruptedException {
        log.info("Game started");
        Scanner scanner = new Scanner(System.in);

        if (gameState.getGameId() == null) {
            initGameState();
        }

        while (gameState.getLives() >= 0) {
            checkLives();
            checkUpgrades();
            startNextTask();

            var gamePointsIteration = gameState.getSequentRuns() * GAME_ITERATION_POINT;
            if (gameState.getScore() > gamePointsIteration) {
                log.info(CONTINUE_CONFIRMATION_MESSAGE.formatted(gamePointsIteration, gameState.getScore()));
                String answer = scanner.nextLine();
                if (!CONTINUE_INPUTS.contains(answer.toLowerCase())) {
                    break;
                }
                gameState.increaseIteration();
            }
        }
    }

    private void checkUpgrades() {
        var nextExpectedUpgrade = itemHandler.getNextExpectedUpgrade(gameState);
        if (nextExpectedUpgrade.isPresent()
                && gameState.getGold() - ShopItem.HEALING_POTION.getExpectedCost() > nextExpectedUpgrade.get().getExpectedCost()) {
            var purchaseStateResponse = itemHandler.purchaseItem(gameState, nextExpectedUpgrade.get().getId());
            purchaseStateResponse.ifPresent(this::updateGameStateAfterPurchaseResponse);
        }
    }

    private void checkLives() {
        if (gameState.getLives() <= 0) {
            throw new GameOverException();
        }
        if (shouldSafeEatBeforeTask()) {
            healUp(gameState);
        }
    }

    private void startNextTask() throws InterruptedException {
        try {
            var nextTask = taskHandler.getNextTask(gameState);

            if (nextTask.isPresent()) {
                gameState.setCurrentTaskId(nextTask.get().getAdId());
                var taskResult = taskHandler.solveTask(gameState.getGameId(), nextTask.get());
                gameState.updateGameStateAfterTaskResponse(taskResult);
            } else {
                log.info("No doable next task found, pass turn");
                gameState.refreshIgnoreList();
                passTurnByBuyingAnyItem();
                log.info("Wait before finding tasks again");
                Thread.sleep(10000);
            }
        } catch (MissingTaskException ignored) {
            gameState.addCurrentTaskToIgnoreList();
            taskHandler.refreshTasks(gameState);
            // Sometimes task list doesn't refresh for some reason, maybe is some cooldown on server side, sleep before retry
            log.info("Wait before finding tasks again");
            Thread.sleep(2000);
            startNextTask();
        }
    }

    private void healUp(GameState gameState) {
        var itemResult = itemHandler.purchaseHealingPotion(gameState);
        if (itemResult.isPresent() && itemResult.get().getLives() > gameState.getLives()) {
            updateGameStateAfterPurchaseResponse(itemResult.get());
            if (gameState.getGold() >= ShopItem.HEALING_POTION.getExpectedCost()) {
                healUp(gameState);
            }
        } else {
            // For some rare cases, after purchasing healing potion, lives are not incremented - heal again
            healUp(gameState);
        }
    }

    private void passTurnByBuyingAnyItem() {
        var purchaseHealingPotion = itemHandler.purchaseHealingPotion(gameState);
        if (purchaseHealingPotion.isPresent()) {
            updateGameStateAfterPurchaseResponse(purchaseHealingPotion.get());
        } else {
            var purchaseCheapestItem = itemHandler.purchaseAnyCheapestItem(gameState);
            purchaseCheapestItem.ifPresentOrElse(
                    this::updateGameStateAfterPurchaseResponse,
                    () -> log.info(NO_ITEMS_AVAILABLE)
            );
        }
    }

    public void initGameState() {
        var gameStartResponse = dataApi.postGameStart();
        gameState.setGameId(gameStartResponse.getGameId());
        gameState.setLives(gameStartResponse.getLives());
        log.info(GAME_STARTED_MESSAGE.formatted(gameState.getGameId(), gameState.getLives(), gameState.getGold()));
    }

    private void updateGameStateAfterPurchaseResponse(PurchaseStateResponse itemResult) {
        gameState.updateGameStateAfterPurchaseResponse(itemResult);
        log.info(itemResult.getItemUsedMessage());
    }

    private boolean shouldSafeEatBeforeTask() {
        return gameState.getLives() == 1 && gameState.getGold() >= ShopItem.HEALING_POTION.getExpectedCost();
    }

    public void showConclusion() {
        log.info(GAME_FINISHED_MESSAGE.formatted(gameState.getGameId(), gameState.getScore(), gameState.getTurn(), gameState.getGold()));
    }
}

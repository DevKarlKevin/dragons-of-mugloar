package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import com.big.dragons_of_mugloar.exception.GameOverException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static com.big.dragons_of_mugloar.GameMessages.GAME_OVER_MESSAGE;
import static com.big.dragons_of_mugloar.enums.ShopItem.CLAW_SHARPENING;
import static com.big.dragons_of_mugloar.mock.MockData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameHandlerTest {
    @Mock
    private MugloarDataApi dataApi;
    @Mock
    private TaskHandler taskHandler;
    @Mock
    private ItemHandler itemHandler;
    private GameHandler gameHandler;
    private GameState gameState;

    @BeforeEach
    public void setUp() {
        gameState = createInitialGameState();
        gameHandler = new GameHandler(gameState, dataApi, taskHandler, itemHandler);
    }

    @Test
    @DisplayName("Should initialize game state after successful game start")
    void shouldInitGameState_whenGameStartResponseReceived() {
        // Given
        when(dataApi.postGameStart()).thenReturn(mockSuccessStartResponse());

        // When
        gameHandler.initGameState();

        // Then
        assertNotNull(gameState.getGameId());
        assertEquals(3, gameState.getLives());
    }

    @Test
    @DisplayName("Should throw exception when player has no lives left")
    void shouldThrowException_whenPlayerHasNoLivesLeft() {
        // Given
        gameState.setLives(0);

        // When
        var gameOverException = assertThrows(GameOverException.class, () -> gameHandler.startGame());

        // Then
        assertEquals(GAME_OVER_MESSAGE, gameOverException.getMessage());
    }

    @Test
    @DisplayName("Purchases a healing potion if the player has only one life left and enough gold to buy one")
    void shouldPurchasePotion_whenPlayerHasOneLifeWithGold() {
        // Given
        gameState.setLives(1);
        gameState.setGold(ShopItem.HEALING_POTION.getExpectedCost());
        var purchaseResponse = mockPurchaseResponse(gameState, ShopItem.HEALING_POTION);
        var taskResponse = mockTaskResponse();

        when(itemHandler.purchaseHealingPotion(gameState)).thenReturn(Optional.of(purchaseResponse));
        when(taskHandler.getNextTask(gameState)).thenReturn(Optional.of(taskResponse));
        when(taskHandler.solveTask(any(), any())).thenReturn(mockTaskStateResponse(gameState, taskResponse.getReward(), false));

        // When
        assertThrows(GameOverException.class, () -> gameHandler.startGame());

        // Then
        verify(itemHandler).purchaseHealingPotion(gameState);
        assertEquals(0, gameState.getLives());
        assertEquals(2, gameState.getTurn());
        assertEquals(taskResponse.getReward(), gameState.getGold());
    }


    @Test
    @DisplayName("Purchases available item if the player has enough gold for upgrade")
    void shouldPurchaseItemUpgrade_whenPlayerHasEnoughGold() {
        // Given
        int initialGold = 151;
        gameState.setLives(3);
        gameState.setGold(initialGold);
        var purchaseResponse = mockPurchaseResponse(gameState, CLAW_SHARPENING);

        when(itemHandler.getNextExpectedUpgrade(gameState)).thenReturn(Optional.of(CLAW_SHARPENING));
        when(itemHandler.purchaseItem(gameState, CLAW_SHARPENING.getId())).thenReturn(Optional.of(purchaseResponse));

        when(taskHandler.getNextTask(gameState))
                .thenThrow(new GameOverException());

        // When
        assertThrows(GameOverException.class, () -> gameHandler.startGame());

        // Then
        verify(itemHandler).purchaseItem(gameState, CLAW_SHARPENING.getId());
        assertEquals(3, gameState.getLives());
        assertEquals(2, gameState.getTurn());
        assertEquals(initialGold - CLAW_SHARPENING.getExpectedCost(), gameState.getGold());
    }

    @Test
    @DisplayName("Starts the next task if there is one available")
    void shouldStartTheNextTask_whenTaskAvailable() {
        // Given
        gameState.setLives(3);
        gameState.setGold(50);
        var taskResponse = mockTaskResponse();

        when(taskHandler.getNextTask(gameState))
                .thenReturn(Optional.of(taskResponse))
                .thenThrow(new GameOverException());
        when(taskHandler.solveTask(any(), any())).thenReturn(mockTaskStateResponse(gameState, 100, true));

        // When
        assertThrows(GameOverException.class, () -> gameHandler.startGame());

        // Then
        verify(taskHandler).solveTask(gameState.getGameId(), taskResponse);
        assertEquals(3, gameState.getLives());
        assertEquals(2, gameState.getTurn());
        assertEquals(100, gameState.getGold());
    }

    @Test
    @DisplayName("Prompts the player to continue playing if they have earned enough points")
    void testContinuePlaying_whenEnoughPoints() {
        // Given
        gameState.setLives(3);
        var taskResponse = mockTaskResponse();

        when(taskHandler.getNextTask(gameState))
                .thenReturn(Optional.of(taskResponse))
                .thenThrow(new GameOverException());
        when(taskHandler.solveTask(any(), any())).thenReturn(mockTaskStateResponse(gameState, 101, true));

        // Simulate user input
        InputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        // When
        assertThrows(GameOverException.class, () -> gameHandler.startGame());

        // Then
        verify(taskHandler).solveTask(gameState.getGameId(), taskResponse);
        assertEquals(3, gameState.getLives());
        assertEquals(2, gameState.getTurn());
        assertEquals(101, gameState.getGold());
    }

    @Test
    @DisplayName("Should pass turn by buying random item when no active task found")
    void shouldPassTurn_whenNoActiveTaskFound() {
        // TODO skipping now
    }

    @Test
    @DisplayName("Should restart getting task when current one has gone missing")
    void shouldRestartTask_whenTaskMissing() {
        // TODO skipping now
    }
}

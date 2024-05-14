package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.big.dragons_of_mugloar.mock.MockData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemHandlerTest {
    @Mock
    private MugloarDataApi dataApi;
    private ItemHandler itemHandler;
    private GameState gameState;

    @BeforeEach
    public void setUp() {
        gameState = createInitialGameState();
        itemHandler = new ItemHandler(dataApi);
    }

    @Test
    @DisplayName("Purchases a healing potion successfully")
    void shouldPurchaseHealingPotionSuccessfully() {
        // Given
        gameState.setGold(50);
        var healingPotion = mockShopItem(ShopItem.HEALING_POTION);
        var purchaseStateResponse = mockPurchaseResponse(gameState, ShopItem.HEALING_POTION);

        when(dataApi.getShopItems(any())).thenReturn(List.of(healingPotion));
        when(dataApi.purchaseShopItem(any(), any())).thenReturn(purchaseStateResponse);

        // When
        var result = itemHandler.purchaseHealingPotion(gameState);

        // Then
        assertTrue(result.isPresent());
        assertEquals(purchaseStateResponse, result.get());
        verify(dataApi).getShopItems(gameState.getGameId());
        verify(dataApi).purchaseShopItem(gameState.getGameId(), ShopItem.HEALING_POTION.getId());
    }

    @Test
    @DisplayName("Fails to purchase a healing potion if insufficient gold")
    void shouldFailToPurchaseHealingPotion_whenInsufficientGold() {
        // Given
        var healingPotion = mockShopItem(ShopItem.HEALING_POTION);
        when(dataApi.getShopItems(any())).thenReturn(List.of(healingPotion));

        // When
        var result = itemHandler.purchaseHealingPotion(gameState);

        // Then
        assertTrue(result.isEmpty());
        verify(dataApi).getShopItems(gameState.getGameId());
        verify(dataApi, never()).purchaseShopItem(any(), any());
    }

    @Test
    @DisplayName("Purchases the cheapest item successfully")
    void shouldPurchaseCheapestItemSuccessfully_whenHasEnoughGold() {
        // Given
        gameState.setGold(100);
        var purchaseStateResponseAffordable = mockPurchaseResponse(gameState, ShopItem.WINGPOT);
        var cheapItem = mockShopItem(ShopItem.WINGPOT);
        var expensiveItem = mockShopItem(ShopItem.POTION_AWESOME_WINGS);

        when(dataApi.getShopItems(any())).thenReturn(List.of(cheapItem, expensiveItem));
        when(dataApi.purchaseShopItem(any(), any())).thenReturn(purchaseStateResponseAffordable);

        // When
        var result = itemHandler.purchaseAnyCheapestItem(gameState);

        // Then
        assertTrue(result.isPresent());
        assertEquals(purchaseStateResponseAffordable, result.get());
        verify(dataApi).getShopItems(gameState.getGameId());
        verify(dataApi).purchaseShopItem(gameState.getGameId(), cheapItem.getId());
    }

    @Test
    @DisplayName("Fails to purchase any item if insufficient gold")
    void shouldFailToPurchaseAnyItem_whenInsufficientGold() {
        // Given
        var expensiveItem = mockShopItem(ShopItem.WINGPOT);
        when(dataApi.getShopItems(any())).thenReturn(List.of(expensiveItem));

        // When
        var result = itemHandler.purchaseAnyCheapestItem(gameState);

        // Then
        assertTrue(result.isEmpty());
        verify(dataApi).getShopItems(gameState.getGameId());
        verify(dataApi, never()).purchaseShopItem(any(), any());
    }

    @Test
    @DisplayName("Gets the next expected upgrade successfully")
    void shouldGetNextExpectedUpgradeSuccessfully() {
        // Given
        gameState.addPurchasedItem(ShopItem.CLAW_SHARPENING.getId());

        var nextExpectedUpgrade = ShopItem.COPPER_PLATING;

        // When
        var result = itemHandler.getNextExpectedUpgrade(gameState);

        // Then
        assertTrue(result.isPresent());
        assertEquals(nextExpectedUpgrade, result.get());
    }

    @Test
    @DisplayName("Returns empty if all upgrades are purchased")
    void shouldReturnEmpty_whenAllUpgradesPurchased() {
        // Given
        Arrays.stream(ShopItem.values()).forEach(item -> gameState.addPurchasedItem(item.getId()));

        // When
        var result = itemHandler.getNextExpectedUpgrade(gameState);

        // Then
        assertTrue(result.isEmpty());
    }
}

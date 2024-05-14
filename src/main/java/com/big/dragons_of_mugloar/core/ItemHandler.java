package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.api.dto.PurchaseStateResponse;
import com.big.dragons_of_mugloar.api.dto.ShopItemResponse;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static com.big.dragons_of_mugloar.enums.ShopItem.HEALING_POTION;

@RequiredArgsConstructor
public class ItemHandler {
    private final MugloarDataApi dataApi;

    public Optional<PurchaseStateResponse> purchaseHealingPotion(GameState gameState) {
        return purchaseItem(gameState, HEALING_POTION.getId());
    }

    public Optional<PurchaseStateResponse> purchaseAnyCheapestItem(GameState gameState) {
        return purchaseItem(gameState, null);
    }

    public Optional<PurchaseStateResponse> purchaseItem(GameState gameState, String itemId) {
        var shopItems = dataApi.getShopItems(gameState.getGameId());

        var itemToPurchase = Optional.ofNullable(itemId)
                .map(id -> shopItems.stream().filter(it -> it.getId().equalsIgnoreCase(itemId)).findFirst())
                .orElseGet(() -> shopItems.stream()
                        .filter(it -> !gameState.itemIsPurchased(it.getId()))
                        .min(Comparator.comparingInt(ShopItemResponse::getCost)));

        if (itemToPurchase.isPresent() && itemToPurchase.get().getCost() <= gameState.getGold()) {
            var purchasedItemResponse = dataApi.purchaseShopItem(gameState.getGameId(), itemToPurchase.get().getId());
            gameState.addPurchasedItem(itemToPurchase.get().getId());
            return Optional.of(purchasedItemResponse);
        }
        return Optional.empty();
    }

    public Optional<ShopItem> getNextExpectedUpgrade(GameState gameState) {
        return Arrays.stream(ShopItem.values())
                .filter(it -> !gameState.getPurchasedItems().contains(it.getId()) && !it.getId().equalsIgnoreCase(HEALING_POTION.getId()))
                .min(Comparator.comparing(ShopItem::getExpectedCost))
                .stream().findFirst();
    }
}

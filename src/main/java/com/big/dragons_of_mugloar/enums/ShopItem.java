package com.big.dragons_of_mugloar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopItem {
    HEALING_POTION("hpot", 50),
    CLAW_SHARPENING("cs", 100),
    COPPER_PLATING("wax", 100),
    GAS("gas", 100),
    BOOK_OF_TRICKS("tricks",100),
    WINGPOT("wingpot", 100),
    CLAW_HONING("ch", 300),
    ROCKET_FUEL("rf", 300),
    IRON_PLATING("iron", 300),
    BOOK_OF_MEGATRICKS("mtrix", 300),
    POTION_AWESOME_WINGS("wingpotmax", 300);

    private final String id;
    private final int expectedCost;
}

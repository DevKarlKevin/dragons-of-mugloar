package com.big.dragons_of_mugloar.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.big.dragons_of_mugloar.GameMessages.ITEM_USED_MESSAGE;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseStateResponse extends StateResponse {
    private boolean shoppingSuccess;
    private int level;

    public String getItemUsedMessage() {
        return ITEM_USED_MESSAGE.formatted(isShoppingSuccess(), getLives(), getGold());
    }
}

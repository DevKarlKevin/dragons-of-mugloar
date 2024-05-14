package com.big.dragons_of_mugloar.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class StateResponse {
    private int lives;
    private int gold;
    private int turn;
}

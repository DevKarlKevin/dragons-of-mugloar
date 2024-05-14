package com.big.dragons_of_mugloar.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskStateResponse extends StateResponse {
    private Boolean success;
    private int score;
    private int highScore;
    private String message;
}

package com.big.dragons_of_mugloar.api.dto;

import lombok.Data;

@Data
public class TaskResponse {
    private String adId;
    private String message;
    private int reward;
    private String probability;
}

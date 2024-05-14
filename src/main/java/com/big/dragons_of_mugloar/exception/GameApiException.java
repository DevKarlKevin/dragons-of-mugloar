package com.big.dragons_of_mugloar.exception;

import static com.big.dragons_of_mugloar.GameMessages.API_INVALID_RESPONSE_MESSAGE;

public class GameApiException extends DragonsOfMugloarException {
    public GameApiException(String message) {
        super(API_INVALID_RESPONSE_MESSAGE.formatted(message));
    }
}

package com.big.dragons_of_mugloar;

import com.big.dragons_of_mugloar.core.GameHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        GameHandler gameHandler = new GameHandler();
        try {
            gameHandler.startGame();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.info("Game finished: " + e.getMessage());
        } finally {
            gameHandler.showConclusion();
        }
    }
}
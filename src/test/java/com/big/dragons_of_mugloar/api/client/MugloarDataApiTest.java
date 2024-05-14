package com.big.dragons_of_mugloar.api.client;

import com.big.dragons_of_mugloar.api.dto.GameStateResponse;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.ShopItem;
import com.big.dragons_of_mugloar.exception.GameOverException;
import com.big.dragons_of_mugloar.exception.MissingTaskException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.big.dragons_of_mugloar.GameMessages.GAME_OVER_MESSAGE;
import static com.big.dragons_of_mugloar.mock.MockData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MugloarDataApiTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private MugloarDataApi dataApi;

    private static final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        dataApi = new MugloarDataApi(httpClient);
    }

    @Test
    @DisplayName("Should start a new game successfully")
    void shouldStartNewGameSuccessfully_whenStartGamePosted() throws IOException, InterruptedException {
        // Given
        var gameStateResponse = mockSuccessStartResponse();
        String jsonResponse = gson.toJson(gameStateResponse);
        mockHttpClientSuccessResponse(jsonResponse);

        // When
        GameStateResponse result = dataApi.postGameStart();

        // Then
        assertNotNull(result);
        assertEquals(MOCK_GAME_ID, result.getGameId());
        assertEquals(3, result.getLives());
    }

    @Test
    @DisplayName("Should retrieve tasks successfully")
    void shouldRetrieveTasksSuccessfully() throws IOException, InterruptedException {
        // Given
        var taskResponse = mockTaskResponse();
        String jsonResponse = gson.toJson(List.of(taskResponse));
        mockHttpClientSuccessResponse(jsonResponse);

        // When
        var result = dataApi.getTasks(MOCK_GAME_ID);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(MOCK_AD_ID, result.getFirst().getAdId());
    }


    @Test
    @DisplayName("Should solve task successfully")
    void shouldSolveTaskSuccessfully() throws IOException, InterruptedException {
        // Given
        var gameState = new GameState();
        gameState.setLives(3);
        var taskStateResponse = mockTaskStateResponse(gameState, 10, true);
        String jsonResponse = gson.toJson(taskStateResponse);
        mockHttpClientSuccessResponse(jsonResponse);

        // When
        var result = dataApi.solveTask(MOCK_GAME_ID, MOCK_AD_ID);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getLives());
        assertEquals(100, result.getScore());
        assertEquals(10, result.getGold());
    }

    @Test
    @DisplayName("Should retrieve shop items successfully")
    void shouldRetrieveShopItemsSuccessfully() throws IOException, InterruptedException {
        // When
        var shopItemResponse = mockShopItem(ShopItem.WINGPOT);
        String jsonResponse = gson.toJson(List.of(shopItemResponse));
        mockHttpClientSuccessResponse(jsonResponse);

        // Given
        var result = dataApi.getShopItems(MOCK_GAME_ID);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ShopItem.WINGPOT.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("Should purchase shop item successfully")
    void shouldPurchaseShopItemSuccessfully() throws IOException, InterruptedException {
        // Given
        var gameState = new GameState();
        var purchaseStateResponse = mockPurchaseResponse(gameState, ShopItem.POTION_AWESOME_WINGS);
        String jsonResponse = gson.toJson(purchaseStateResponse);
        mockHttpClientSuccessResponse(jsonResponse);

        // When
        var result = dataApi.purchaseShopItem(MOCK_GAME_ID, ShopItem.POTION_AWESOME_WINGS.getId());

        // Then
        assertNotNull(result);
        assertTrue(result.isShoppingSuccess());
    }

    @Test
    @DisplayName("Should throw MissingTaskException on 400 response")
    void shouldThrowMissingTaskException_when400Response() throws IOException, InterruptedException {
        // Given
        var response = "Task not found";
        mockHttpClientFailureResponse(400, response);

        // When
        var exception = assertThrows(MissingTaskException.class, () -> {
            dataApi.getTasks(MOCK_GAME_ID);
        });

        // Then
        assertEquals(exception.getMessage(), response);
    }

    @Test
    @DisplayName("Should throw GameOverException on 410 response")
    void shouldThrowGameOverException_when410Response() throws IOException, InterruptedException {
        // Given
        mockHttpClientFailureResponse(410, "");

        // When
        var exception = assertThrows(GameOverException.class, () -> dataApi.getTasks(MOCK_GAME_ID));

        // Then
        assertEquals(exception.getMessage(), GAME_OVER_MESSAGE);
    }

    private void mockHttpClientFailureResponse(int httpStatus, String response) throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(httpStatus);
        if (!response.isEmpty()) {
            when(httpResponse.body()).thenReturn(response);
        }
    }

    private void mockHttpClientSuccessResponse(String jsonResponse) throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
    }
}

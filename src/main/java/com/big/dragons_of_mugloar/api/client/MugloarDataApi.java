package com.big.dragons_of_mugloar.api.client;

import com.big.dragons_of_mugloar.api.dto.*;
import com.big.dragons_of_mugloar.exception.DragonsOfMugloarException;
import com.big.dragons_of_mugloar.exception.GameApiException;
import com.big.dragons_of_mugloar.exception.GameOverException;
import com.big.dragons_of_mugloar.exception.MissingTaskException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static com.big.dragons_of_mugloar.GameMessages.HTTP_REQUEST_FAILED;
import static com.big.dragons_of_mugloar.api.ApiConstants.*;

@NoArgsConstructor
@AllArgsConstructor
public class MugloarDataApi {
    private HttpClient httpClient = HttpClient.newBuilder().build();

    public GameStateResponse postGameStart() {
        var request = postRequest(API_GAME_START);
        return sendRequest(request, GameStateResponse.class);
    }

    public List<TaskResponse> getTasks(String gameId) {
        var request = getRequest(API_GET_TASKS.formatted(gameId));
        return sendRequest(request, new TypeToken<ArrayList<TaskResponse>>(){}.getType());
    }

    public TaskStateResponse solveTask(String gameId, String taskId) {
        var request = postRequest(API_SOLVE_TASK.formatted(gameId, taskId));
        return sendRequest(request, TaskStateResponse.class);
    }

    public List<ShopItemResponse> getShopItems(String gameId) {
        var request = getRequest(API_GET_SHOP_LIST.formatted(gameId));
        return sendRequest(request, new TypeToken<ArrayList<ShopItemResponse>>(){}.getType());
    }

    public PurchaseStateResponse purchaseShopItem(String gameId, String itemId) {
        var request = postRequest(API_SHOP_ITEM.formatted(gameId, itemId));
        return sendRequest(request, PurchaseStateResponse.class);
    }

    private static HttpRequest getRequest(String url) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
    }

    private static HttpRequest postRequest(String url) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(url))
                .build();
    }

    private <T> T sendRequest(HttpRequest request, Type clazz) {
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return switch (response.statusCode()) {
                case 200 -> new Gson().fromJson(response.body(), clazz);
                case 400 -> throw new MissingTaskException(response.body());
                case 410 -> throw new GameOverException();
                default -> throw new DragonsOfMugloarException(HTTP_REQUEST_FAILED.formatted(response.statusCode(), response.body()));
            };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new GameApiException(e.getMessage());
        }
        return null;
    }
}

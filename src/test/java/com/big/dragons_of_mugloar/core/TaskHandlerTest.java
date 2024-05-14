package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.TaskProbability;
import com.big.dragons_of_mugloar.exception.DragonsOfMugloarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.big.dragons_of_mugloar.mock.MockData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskHandlerTest {
    @Mock
    private MugloarDataApi dataApi;
    private TaskHandler taskHandler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = createInitialGameState();
        taskHandler = new TaskHandler(dataApi);
    }

    @Test
    @DisplayName("Solves task successfully")
    void shouldSolveTaskSuccessfully() {
        // Given
        var taskResponse = mockTaskResponse();
        var taskStateResponse = mockTaskStateResponse(gameState, 10, true);

        when(dataApi.solveTask(any(), any())).thenReturn(taskStateResponse);

        // When
        var result = taskHandler.solveTask(gameState.getGameId(), taskResponse);

        // Then
        assertEquals(taskStateResponse, result);
        verify(dataApi).solveTask(gameState.getGameId(), taskResponse.getAdId());
    }

    @Test
    @DisplayName("Gets the next task successfully")
    void shouldGetNextTaskSuccessfully() {
        // Given
        var taskResponse = mockTaskResponse();

        when(dataApi.getTasks(any())).thenReturn(List.of(taskResponse));

        // When
        var result = taskHandler.getNextTask(gameState);

        // Then
        assertTrue(result.isPresent());
        assertEquals(taskResponse, result.get());
        verify(dataApi).getTasks(gameState.getGameId());
    }

    @Test
    @DisplayName("Returns empty when no task is available")
    void shouldReturnEmptyWhenNoTaskAvailable() {
        // Given
        when(dataApi.getTasks(any())).thenReturn(List.of());

        // When
        assertThrows(DragonsOfMugloarException.class, () -> taskHandler.getNextTask(gameState));

        // Then
        verify(dataApi).getTasks(gameState.getGameId());
    }

    @Test
    @DisplayName("Filters out unknown tasks")
    void shouldFilterOutUnknownTasks() {
        // Given
        var taskResponse1 = mockTaskResponse();
        taskResponse1.setProbability(TaskProbability.UNKNOWN.getProbability());
        var taskResponse2 = mockTaskResponse();

        // When
        when(dataApi.getTasks(any())).thenReturn(List.of(taskResponse1, taskResponse2));

        var result = taskHandler.getNextTask(gameState);

        // Then
        assertTrue(result.isPresent());
        assertEquals(taskResponse2, result.get());
        verify(dataApi).getTasks(gameState.getGameId());
    }
}

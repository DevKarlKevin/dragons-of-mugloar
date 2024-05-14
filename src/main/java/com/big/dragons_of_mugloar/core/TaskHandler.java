package com.big.dragons_of_mugloar.core;

import com.big.dragons_of_mugloar.api.client.MugloarDataApi;
import com.big.dragons_of_mugloar.api.dto.TaskResponse;
import com.big.dragons_of_mugloar.api.dto.TaskStateResponse;
import com.big.dragons_of_mugloar.entity.GameState;
import com.big.dragons_of_mugloar.enums.TaskProbability;
import com.big.dragons_of_mugloar.exception.DragonsOfMugloarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.big.dragons_of_mugloar.GameMessages.NO_TASKS_AVAILABLE;
import static com.big.dragons_of_mugloar.GameMessages.TASK_SOLVE_RESULT_MESSAGE;

@Slf4j
@RequiredArgsConstructor
public class TaskHandler {
    private final MugloarDataApi dataApi;

    public TaskStateResponse solveTask(String gameId, TaskResponse nextTask) {
        var taskResponse = dataApi.solveTask(gameId, nextTask.getAdId());
        log.info(TASK_SOLVE_RESULT_MESSAGE.formatted(
                taskResponse.getMessage(),
                taskResponse.getLives(),
                taskResponse.getScore(),
                nextTask.getMessage())
        );
        return taskResponse;
    }

    public Optional<TaskResponse> getNextTask(GameState gameState) {
        var tasks = dataApi.getTasks(gameState.getGameId());
        return filterNextOptimalTask(tasks, gameState.getIgnoreTaskIds());
    }

    public void refreshTasks(GameState gameState) {
        dataApi.getTasks(gameState.getGameId());
    }

    private Optional<TaskResponse> filterNextOptimalTask(List<TaskResponse> tasks, List<String> ignoreAdIds) {
        if (tasks.isEmpty()) {
            throw new DragonsOfMugloarException(NO_TASKS_AVAILABLE);
        }

        return tasks.stream()
                .filter(task -> {
                    var difficulty = TaskProbability.getByProbability(task.getProbability());
                    return !ignoreAdIds.contains(task.getAdId()) && difficulty != TaskProbability.UNKNOWN;
                })
                .sorted(Comparator.comparing(TaskResponse::getReward).reversed())
                .min(Comparator.comparingInt(task ->
                        TaskProbability.getByProbability(task.getProbability()).getDifficulty())
                );
    }
}

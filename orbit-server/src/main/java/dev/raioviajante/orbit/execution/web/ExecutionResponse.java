package dev.raioviajante.orbit.execution.web;

import java.time.Instant;

import dev.raioviajante.orbit.execution.domain.Execution;
import dev.raioviajante.orbit.execution.domain.ExecutionStatus;

public record ExecutionResponse(
        Long id,
        Long jobId,
        ExecutionStatus status,
        String command,
        int maxRetries,
        int timeoutSeconds,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Integer exitCode,
        String errorMessage
) {

    // Converts the domain entity into the representation exposed by the HTTP API.
    public static ExecutionResponse from(Execution execution) {
        return new ExecutionResponse(
                execution.getId(),
                execution.getJob().getId(),
                execution.getStatus(),
                execution.getCommand(),
                execution.getMaxRetries(),
                execution.getTimeoutSeconds(),
                execution.getCreatedAt(),
                execution.getStartedAt(),
                execution.getFinishedAt(),
                execution.getExitCode(),
                execution.getErrorMessage()
        );
    }
}
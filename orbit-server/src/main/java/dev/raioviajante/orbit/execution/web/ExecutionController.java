package dev.raioviajante.orbit.execution.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.raioviajante.orbit.execution.application.ExecutionService;
import dev.raioviajante.orbit.execution.domain.Execution;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController 
public class ExecutionController {
    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/jobs/{jobId}/executions")
    @ResponseStatus(HttpStatus.CREATED)
    public ExecutionResponse createForJob(@PathVariable Long jobId) {
        try {
            Execution execution = executionService.createForJob(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

            return ExecutionResponse.from(execution);

        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception
            );
        }
    }
}

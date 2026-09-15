package dev.raioviajante.orbit.execution.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.raioviajante.orbit.execution.application.ExecutionService;
import dev.raioviajante.orbit.execution.domain.Execution;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/executions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExecutionResponse findById(@PathVariable Long id) {
        Execution execution = executionService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Execution not found"));

        return ExecutionResponse.from(execution);
    }
    
    @GetMapping("/jobs/{jobId}/executions")
    @ResponseStatus(HttpStatus.OK)
    public List<ExecutionResponse> findAllByJobId(@PathVariable Long jobId) {
        List<Execution> executions = executionService.findAllByJobId(jobId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        // Converts each domain entity into the representation exposed by the API.
        return executions.stream()
            .map(ExecutionResponse::from)
            .toList();
    }
}

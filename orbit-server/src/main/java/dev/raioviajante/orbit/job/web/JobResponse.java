package dev.raioviajante.orbit.job.web;

import java.time.Instant;

import dev.raioviajante.orbit.job.domain.Job;

public record JobResponse(Long id,
                          String name,
                          String command,
                          String cronExpression,
                          boolean enabled,
                          int maxRetries,
                          int timeoutSeconds,
                          Instant createdAt,
                          Instant updatedAt) 
{
    // Converts the domain entity into the representation exposed by the HTTP API.
    public static JobResponse from(Job job) {
        
        // Explicitly maps only the fields that should be returned to API clients.
        return new JobResponse(
                            job.getId(),
                            job.getName(),
                            job.getCommand(),
                            job.getCronExpression(),
                            job.isEnabled(),
                            job.getMaxRetries(),
                            job.getTimeoutSeconds(),
                            job.getCreatedAt(),
                            job.getUpdatedAt());
    }
}

package dev.raioviajante.orbit.job.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateJobRequest(
    @NotBlank 
    @Size(max = 120)
    String name,

    @NotBlank 
    String command,

    @Size (max = 100)
    @Pattern(regexp = ".*\\S.*", message = "must not be blank")
    String cronExpression,

    @NotNull 
    @PositiveOrZero 
    Integer maxRetries,

    @NotNull 
    @Positive 
    Integer timeoutSeconds) {}

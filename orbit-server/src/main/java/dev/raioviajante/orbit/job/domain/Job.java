package dev.raioviajante.orbit.job.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT") 
    private String command;

    @Column(name = "cron_expression", length = 100)
    private String cronExpression;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "timeout_seconds", nullable = false)
    private int timeoutSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Job() {
    }

    public Job(String name, 
               String command, 
               String cronExpression, 
               int maxRetries, 
               int timeoutSeconds) {
        validateName(name);
        validateCommand(command);
        validateCronExpression(cronExpression);
        validateMaxRetries(maxRetries);
        validateTimeoutSeconds(timeoutSeconds);
        this.name = name;
        this.command = command;
        this.cronExpression = cronExpression;
        this.enabled = true;
        this.maxRetries = maxRetries;
        this.timeoutSeconds = timeoutSeconds;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = Instant.now();
    }

    public void updateConfiguration(String name, 
                                    String command, 
                                    String cronExpression, 
                                    int maxRetries, 
                                    int timeoutSeconds) {
        validateName(name);
        validateCommand(command);
        validateCronExpression(cronExpression);
        validateMaxRetries(maxRetries);
        validateTimeoutSeconds(timeoutSeconds);
        this.name = name;
        this.command = command;
        this.cronExpression = cronExpression;
        this.maxRetries = maxRetries;
        this.timeoutSeconds = timeoutSeconds;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    
    public String getName() { return name; }

    public String getCommand() { return command; }

    public String getCronExpression() { return cronExpression; }

    public boolean isEnabled() { return enabled; }

    public int getMaxRetries() { return maxRetries; }

    public int getTimeoutSeconds() { return timeoutSeconds; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Job name must not be blank");
        
        if (name.length() > 120) throw new IllegalArgumentException("Job name must not exceed 120 characters");
    }

    private static void validateCommand(String command) {
        if (command == null || command.isBlank()) throw new IllegalArgumentException("Job command must not be blank");
    }

    private static void validateCronExpression(String cronExpression) {
        if (cronExpression != null && cronExpression.isBlank()) throw new IllegalArgumentException("Cron expression must not be blank");
    }

    private static void validateMaxRetries(int maxRetries) {
        if (maxRetries < 0) throw new IllegalArgumentException("Max retries must be greater than or equal to zero");
    }

    private static void validateTimeoutSeconds(int timeoutSeconds) {
        if (timeoutSeconds <= 0) throw new IllegalArgumentException("Timeout seconds must be greater than zero");   
    }
}

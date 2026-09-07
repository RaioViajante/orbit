package dev.raioviajante.orbit.execution.domain;

import java.time.Instant;

import dev.raioviajante.orbit.job.domain.Job;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "executions")
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String command;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "timeout_seconds", nullable = false)
    private int timeoutSeconds;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    protected Execution() {
    }

    public Execution(Job job) {
        if (job == null) throw new IllegalArgumentException("Job must not be null");
        this.job = job;
        this.status = ExecutionStatus.QUEUED;
        this.command = job.getCommand();
        this.maxRetries = job.getMaxRetries();
        this.timeoutSeconds = job.getTimeoutSeconds();
        this.createdAt = Instant.now();
    }
    public Long getId() { return id; }

    public Job getJob() { return job; }

    public ExecutionStatus getStatus() { return status; }

    public String getCommand() { return command; }

    public int getMaxRetries() { return maxRetries; }

    public int getTimeoutSeconds() { return timeoutSeconds; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getStartedAt() { return startedAt; }

    public Instant getFinishedAt() { return finishedAt; }

    public Integer getExitCode() { return exitCode; }

    public String getErrorMessage() { return errorMessage; }

    public void start() {
        if (status != ExecutionStatus.QUEUED) throw new IllegalStateException("Only queued executions can be started");
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void succeed(int exitCode) {
        if (status != ExecutionStatus.RUNNING) throw new IllegalStateException("Only running executions can succeed");
        this.status = ExecutionStatus.SUCCEEDED;
        this.exitCode = exitCode;
        this.finishedAt = Instant.now();
    }

    public void fail(int exitCode, String errorMessage) {
        if (status != ExecutionStatus.RUNNING) throw new IllegalStateException("Only running executions can fail");
        if (errorMessage == null || errorMessage.isBlank())
            throw new IllegalArgumentException("Error message must not be blank");

        this.status = ExecutionStatus.FAILED;
        this.exitCode = exitCode;
        this.errorMessage = errorMessage;
        this.finishedAt = Instant.now();
    }

    public void cancel() {
        if (status != ExecutionStatus.QUEUED && status != ExecutionStatus.RUNNING)
            throw new IllegalStateException("Only queued or running executions can be cancelled");

        this.status = ExecutionStatus.CANCELLED;
        this.finishedAt = Instant.now();
    }
}

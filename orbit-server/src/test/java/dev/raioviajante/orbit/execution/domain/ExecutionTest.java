package dev.raioviajante.orbit.execution.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import dev.raioviajante.orbit.job.domain.Job;

class ExecutionTest {
    @Test
    void shouldCreateQueuedExecutionFromJobSnapshot() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        assertThat(execution.getJob()).isSameAs(job);
        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.QUEUED);
        assertThat(execution.getCommand()).isEqualTo("./backup.sh");
        assertThat(execution.getMaxRetries()).isEqualTo(3);
        assertThat(execution.getTimeoutSeconds()).isEqualTo(300);
        assertThat(execution.getCreatedAt()).isNotNull();
        assertThat(execution.getStartedAt()).isNull();
        assertThat(execution.getFinishedAt()).isNull();
        assertThat(execution.getExitCode()).isNull();
        assertThat(execution.getErrorMessage()).isNull();
    }

    @Test
    void shouldStartQueuedExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();

        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.RUNNING);
        assertThat(execution.getStartedAt()).isNotNull();
        assertThat(execution.getFinishedAt()).isNull();
    }

    @Test
    void shouldRejectStartingExecutionThatIsNotQueued() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();

        assertThatThrownBy(execution::start).isInstanceOf(IllegalStateException.class)
            .hasMessage("Only queued executions can be started");
    }

    @Test
    void shouldSucceedRunningExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();
        execution.succeed(0);

        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.SUCCEEDED);
        assertThat(execution.getExitCode()).isEqualTo(0);
        assertThat(execution.getFinishedAt()).isNotNull();
        assertThat(execution.getErrorMessage()).isNull();
    }

    @Test
    void shouldRejectSucceedingExecutionThatIsNotRunning() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);
  
        Execution execution = new Execution(job);

        assertThatThrownBy(() -> execution.succeed(0)).isInstanceOf(IllegalStateException.class)
            .hasMessage("Only running executions can succeed");
    }

    @Test
    void shouldFailRunningExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();
        execution.fail(1, "Backup command failed");

        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.FAILED);
        assertThat(execution.getExitCode()).isEqualTo(1);
        assertThat(execution.getErrorMessage()).isEqualTo("Backup command failed");
        assertThat(execution.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldRejectFailingExecutionThatIsNotRunning() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        assertThatThrownBy(() -> execution.fail(1, "Backup command failed")).isInstanceOf(IllegalStateException.class)
            .hasMessage("Only running executions can fail");
    }

    @Test
    void shouldRejectBlankErrorMessage() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);
        execution.start();

        assertThatThrownBy(() -> execution.fail(1, " ")).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Error message must not be blank");
    }

    @Test
    void shouldCancelQueuedExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.cancel();

        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.CANCELLED);
        assertThat(execution.getFinishedAt()).isNotNull();
        assertThat(execution.getStartedAt()).isNull();
    }

    @Test
    void shouldCancelRunningExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();
        execution.cancel();

        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.CANCELLED);
        assertThat(execution.getStartedAt()).isNotNull();
        assertThat(execution.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldRejectCancellingFinishedExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Execution execution = new Execution(job);

        execution.start();
        execution.succeed(0);

        assertThatThrownBy(execution::cancel).isInstanceOf(IllegalStateException.class)
            .hasMessage("Only queued or running executions can be cancelled");
    }
}

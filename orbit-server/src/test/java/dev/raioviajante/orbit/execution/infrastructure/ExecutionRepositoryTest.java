package dev.raioviajante.orbit.execution.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.raioviajante.orbit.execution.domain.Execution;
import dev.raioviajante.orbit.execution.domain.ExecutionStatus;
import dev.raioviajante.orbit.job.domain.Job;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;
import jakarta.persistence.EntityManager;

@DataJpaTest 
@Testcontainers 
public class ExecutionRepositoryTest {
    @Container
    @ServiceConnection 
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistExecution() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300);

        Job savedJob = jobRepository.save(job);

        Execution execution = new Execution(savedJob);

        Execution savedExecution = executionRepository.save(execution);

        assertThat(savedExecution.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Execution foundExecution = executionRepository.findById(savedExecution.getId())
            .orElseThrow();

        assertThat(foundExecution.getStatus()).isEqualTo(ExecutionStatus.QUEUED);

        assertThat(foundExecution.getCommand()).isEqualTo("./backup.sh");

        assertThat(foundExecution.getMaxRetries()).isEqualTo(3);

        assertThat(foundExecution.getTimeoutSeconds()).isEqualTo(300);

        assertThat(foundExecution.getCreatedAt()).isNotNull();
        assertThat(foundExecution.getStartedAt()).isNull();
        assertThat(foundExecution.getFinishedAt()).isNull();
        assertThat(foundExecution.getExitCode()).isNull();
        assertThat(foundExecution.getErrorMessage()).isNull();

        assertThat(foundExecution.getJob().getId())
            .isEqualTo(savedJob.getId());
    }

}

package dev.raioviajante.orbit.execution.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.raioviajante.orbit.execution.domain.Execution;
import dev.raioviajante.orbit.execution.domain.ExecutionStatus;
import dev.raioviajante.orbit.execution.infrastructure.ExecutionRepository;
import dev.raioviajante.orbit.job.domain.Job;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;

class ExecutionServiceTest {

    @Mock
    private ExecutionRepository executionRepository;

    @Mock
    private JobRepository jobRepository;

    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        executionService = new ExecutionService(
                executionRepository,
                jobRepository
        );
    }

    @Test
    void shouldCreateExecutionForExistingJob() {
        Job job = new Job(
                "database-backup",
                "./backup.sh",
                null,
                3,
                300
        );

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(executionRepository.save(any(Execution.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Execution> result = executionService.createForJob(1L);

        assertThat(result).isPresent();

        Execution execution = result.orElseThrow();

        assertThat(execution.getJob()).isSameAs(job);
        assertThat(execution.getStatus()).isEqualTo(ExecutionStatus.QUEUED);
        assertThat(execution.getCommand()).isEqualTo("./backup.sh");
        assertThat(execution.getMaxRetries()).isEqualTo(3);
        assertThat(execution.getTimeoutSeconds()).isEqualTo(300);

        verify(jobRepository).findById(1L);
        verify(executionRepository).save(any(Execution.class));
    }

    @Test
    void shouldReturnEmptyWhenJobDoesNotExist() {
        when(jobRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Execution> result = executionService.createForJob(999L);

        assertThat(result).isEmpty();

        verify(jobRepository).findById(999L);

        // No Execution should be persisted when the requested Job does not exist.
        verify(executionRepository, never()).save(any(Execution.class));
    }
}
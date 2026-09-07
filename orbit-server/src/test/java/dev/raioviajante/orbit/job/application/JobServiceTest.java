package dev.raioviajante.orbit.job.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.raioviajante.orbit.job.domain.Job;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    private JobService jobService;

    // Creates fresh mocks and a new service instance before each test.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobService = new JobService(jobRepository);
    }

    @Test
    void shouldCreateJob() {
        // Makes the mocked repository return the same Job instance it receives.
        when(jobRepository.save(any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Job createdJob = jobService.create(
                "database-backup",
                "./backup.sh",
                null,
                3,
                300
        );

        assertThat(createdJob.getName()).isEqualTo("database-backup");
        assertThat(createdJob.getCommand()).isEqualTo("./backup.sh");
        assertThat(createdJob.getMaxRetries()).isEqualTo(3);
        assertThat(createdJob.getTimeoutSeconds()).isEqualTo(300);
        assertThat(createdJob.isEnabled()).isTrue();

        // Confirms that the service requested persistence exactly once.
        verify(jobRepository).save(any(Job.class));
    }
}
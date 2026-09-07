package dev.raioviajante.orbit.job.application;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Test 
    void shouldDisableJob() {
        Job job = new Job(
            "database-backup",
            "./backup.sh",
            null,
            3,
            300);
        
        // Simulates an existing job being returned by the repository.
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        Optional<Job> result = jobService.disable(1L);

        // Confirms that the service executed the domain behavior successfully.
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().isEnabled()).isFalse();
        verify(jobRepository).findById(1L);
    }

    @Test
    void shouldEnableJob() {
        Job job = new Job(
                "database-backup",
                "./backup.sh",
                null,
                3,
                300);

        // Ensures the job starts disabled so the enable behavior is actually tested.
        job.disable();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        Optional<Job> result = jobService.enable(1L);

        // Confirms that the service executed the domain behavior successfully.
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().isEnabled()).isTrue();
        verify(jobRepository).findById(1L);
    }

    @Test 
    void shouldFindJobById() {
        Job job = new Job(
                    "database-backup",
                    "./backup",
                    null,
                    3,
                    300);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        Optional<Job> result = jobService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isSameAs(job);
        verify(jobRepository).findById(1L);
    }

    @Test 
    void shouldReturnEmptyWhenJobDoesNotExist() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Job> result = jobService.findById(999L);
        assertThat(result).isEmpty();
        verify(jobRepository).findById(999L);
    }

    @Test
    void shouldFindAllJobs() {
        Job firstJob = new Job(
                "database-backup",
                "./backup.sh",
                null,
                3,
                300);

        Job secondJob = new Job(
                "cleanup-temp",
                "./cleanup.sh",
                null,
                1,
                120);

        when(jobRepository.findAll()).thenReturn(List.of(firstJob, secondJob));
        List<Job> result = jobService.findAll();
        assertThat(result).containsExactly(firstJob, secondJob);
        verify(jobRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoJobsExist() {
        when(jobRepository.findAll()).thenReturn(List.of());
        List<Job> result = jobService.findAll();
        assertThat(result).isEmpty();
        verify(jobRepository).findAll();
    }

    @Test
    void shouldUpdateJobConfiguration() {
        Job job = new Job(
                "database-backup",
                "./backup.sh",
                null,
                3,
                300);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Optional<Job> result = jobService.updateConfiguration(
                1L,
                "nightly-backup",
                "./nightly-backup.sh",
                "0 0 * * *",
                5,
                600);

        Job updatedJob = result.orElseThrow();

        assertThat(updatedJob.getName()).isEqualTo("nightly-backup");
        assertThat(updatedJob.getCommand()).isEqualTo("./nightly-backup.sh");
        assertThat(updatedJob.getCronExpression()).isEqualTo("0 0 * * *");
        assertThat(updatedJob.getMaxRetries()).isEqualTo(5);
        assertThat(updatedJob.getTimeoutSeconds()).isEqualTo(600);

        verify(jobRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUpdatingMissingJob() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Job> result = jobService.updateConfiguration(
                999L,
                "nightly-backup",
                "./nightly-backup.sh",
                null,
                5,
                600);

        assertThat(result).isEmpty();
        verify(jobRepository).findById(999L);
    }
}
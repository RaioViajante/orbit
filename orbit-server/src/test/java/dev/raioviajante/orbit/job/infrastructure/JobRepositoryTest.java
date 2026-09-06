package dev.raioviajante.orbit.job.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.raioviajante.orbit.job.domain.Job;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Testcontainers
class JobRepositoryTest {
    @Container 
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private JobRepository jobRepository;

    @Autowired 
    private EntityManager entityManager;

    @Test
    void shouldPersistJob() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300 );

        Job savedJob = jobRepository.save(job);

        // Confirms that the database generated an ID for the persisted job.
        assertThat(savedJob.getId()).isNotNull();

        // Forces Hibernate to synchronize pending changes with the database.
        entityManager.flush();

        // Clears the persistence context so the job must be loaded from the database again.
        entityManager.clear();

        Job foundJob = jobRepository.findById(savedJob.getId()).orElseThrow();

        // Confirms that the persisted job can be loaded back with the expected state.
        assertThat(foundJob.getName()).isEqualTo("database-backup");
        assertThat(foundJob.getCommand()).isEqualTo("./backup.sh");
        assertThat(foundJob.getCronExpression()).isNull();
        assertThat(foundJob.getMaxRetries()).isEqualTo(3);
        assertThat(foundJob.getTimeoutSeconds()).isEqualTo(300);
        assertThat(foundJob.isEnabled()).isTrue();
    }
}

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

@DataJpaTest
@Testcontainers
class JobRepositoryTest {
    @Container 
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private JobRepository jobRepository;

    @Test
    void shouldPersistJob() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300 );

        Job savedJob = jobRepository.save(job);
        
        // Verifies that PostgreSQL generated an ID after the job was persisted.
        assertThat(savedJob.getId()).isNotNull();
    }
}

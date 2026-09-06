package dev.raioviajante.orbit.job.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.raioviajante.orbit.job.domain.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}

package dev.raioviajante.orbit.execution.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.raioviajante.orbit.execution.domain.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    List<Execution> findAllByJob_IdOrderByCreatedAtDesc(Long jobId);
}
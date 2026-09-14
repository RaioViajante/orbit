package dev.raioviajante.orbit.execution.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.raioviajante.orbit.execution.domain.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
}
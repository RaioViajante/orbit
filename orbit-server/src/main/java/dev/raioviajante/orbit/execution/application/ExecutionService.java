package dev.raioviajante.orbit.execution.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.raioviajante.orbit.execution.domain.Execution;
import dev.raioviajante.orbit.execution.infrastructure.ExecutionRepository;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;

@Service 
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final JobRepository jobRepository;

    public ExecutionService(ExecutionRepository executionRepository, JobRepository jobRepository) {
        this.executionRepository = executionRepository;
        this.jobRepository = jobRepository;
    }

    public Optional<Execution> createForJob(Long jobId) {
        return jobRepository.findById(jobId).map(job ->
            {
                Execution execution = new Execution(job);
                return executionRepository.save(execution);
            }
        );
    }
}

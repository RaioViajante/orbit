package dev.raioviajante.orbit.job.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.raioviajante.orbit.job.domain.Job;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;
import jakarta.transaction.Transactional;

@Service 
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job create(String name,
                      String command,
                      String cronExpression,
                      int maxRetries,
                      int timeoutSeconds) {

        Job job = new Job(name,
                          command,
                          cronExpression,
                          maxRetries,
                          timeoutSeconds);

        return jobRepository.save(job);
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    // Returns an Optional because the requested job may not exist.
    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    @Transactional 
    public Optional<Job> updateConfiguration(Long id,
                                             String name,
                                             String command,
                                             String cronExpression,
                                             int maxRetries,
                                             int timeoutSeconds) 
    {
        return jobRepository.findById(id).map(job -> 
            {
                job.updateConfiguration(name, command, cronExpression, maxRetries, timeoutSeconds);
                // The managed entity is persisted automatically by JPA dirty checking.
                return job;
            }
        );
    }
}

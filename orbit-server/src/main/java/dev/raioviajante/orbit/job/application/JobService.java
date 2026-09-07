package dev.raioviajante.orbit.job.application;

import org.springframework.stereotype.Service;

import dev.raioviajante.orbit.job.domain.Job;
import dev.raioviajante.orbit.job.infrastructure.JobRepository;

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
}

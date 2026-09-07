package dev.raioviajante.orbit.job.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.raioviajante.orbit.job.application.JobService;
import dev.raioviajante.orbit.job.domain.Job;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@Valid @RequestBody CreateJobRequest request) {
        // Delegates the use case to the application layer after HTTP input validation.
        Job job = jobService.create(request.name(),
                                 request.command(),
                                 request.cronExpression(),
                                 request.maxRetries(),
                                 request.timeoutSeconds());
        return JobResponse.from(job);
    }

}

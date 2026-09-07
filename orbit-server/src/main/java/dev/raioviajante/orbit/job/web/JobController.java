package dev.raioviajante.orbit.job.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping 
    @ResponseStatus(HttpStatus.OK)
    public List<JobResponse> findAll() {
        // Converts persisted domain entities into the representation exposed by the API.
        return jobService.findAll().stream().map(JobResponse::from).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse findById(@PathVariable Long id) {
        // Returns 404 when the requested job does not exist.
        Job job = jobService.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found")
        );
        return JobResponse.from(job);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse update(@PathVariable Long id, 
                              @Valid @RequestBody UpdateJobRequest request)
    {   
        // Returns 404 when the job to update does not exist.
        Job job = jobService.updateConfiguration(id,
                                                 request.name(),
                                                 request.command(),
                                                 request.cronExpression(),
                                                 request.maxRetries(),
                                                 request.timeoutSeconds())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Job not found"));

        return JobResponse.from(job);
    }

    @PostMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse disable(@PathVariable Long id) {
        Job job = jobService.disable(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found")
        );
        return JobResponse.from(job);
    }

    @PostMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.OK)
    public JobResponse enable(@PathVariable Long id) {
        Job job = jobService.enable(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Job not found")
        );
        return JobResponse.from(job);
    }
}
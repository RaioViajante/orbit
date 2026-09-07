package dev.raioviajante.orbit.job.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class JobTest {
    @Test
    void shouldCreateValidJob() {
        Job job = new Job("database-backup",
                          "./backup.sh",
                          null,
                          3,
                          300
        );

        assertThat(job.getName()).isEqualTo("database-backup");
        assertThat(job.getCommand()).isEqualTo("./backup.sh");
        assertThat(job.getCronExpression()).isNull();
        assertThat(job.getMaxRetries()).isEqualTo(3);
        assertThat(job.getTimeoutSeconds()).isEqualTo(300);
        assertThat(job.isEnabled()).isTrue();
        assertThat(job.getCreatedAt()).isNotNull();
        assertThat(job.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> 
            new Job(
                    "  ",
                    "./backup.sh",
                    null,
                    3,
                    300
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Job name must not be blank");
    }

    @Test
    void shouldRejectTooLongName() {
        String name = "a".repeat(121);

        assertThatThrownBy(() ->
            new Job(
                    name,
                    "./backup.sh",
                    null,
                    3,
                    300
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Job name must not exceed 120 characters");
    }

    @Test
    void shouldRejectBlankCommand() {
        assertThatThrownBy(() ->
            new Job(
                    "database-backup",
                    "  ",
                    null,
                    3,
                    300
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Job command must not be blank");
    }

    @Test
    void shouldRejectBlankCronExpression() {
        assertThatThrownBy(() ->
            new Job(
                    "database-backup",
                    "./backup.sh",
                    "  ",
                    3,
                    300
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Cron expression must not be blank");
    }

    @Test
    void shouldRejectNegativeMaxRetries() {
        assertThatThrownBy(() ->
            new Job(
                    "database-backup",
                    "./backup",
                    null,
                    -1,
                    300
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Max retries must be greater than or equal to zero");
    }

    @Test 
    void shouldRejectNonPositiveTimeout() {
        assertThatThrownBy(() ->
            new Job(
                    "database-backup",
                    "./backup",
                    null,
                    3,
                    0
            )
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Timeout seconds must be greater than zero");
    }
}

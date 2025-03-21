package project.api.rest.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;


@Getter
@Setter
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;

    public ErrorResponse(int status, String error, String message, Map<String, String> details) {
        this.timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

}

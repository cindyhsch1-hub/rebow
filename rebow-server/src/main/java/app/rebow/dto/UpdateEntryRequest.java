package app.rebow.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class UpdateEntryRequest {

    @NotEmpty(message = "petIds must not be empty")
    private List<Long> petIds;

    private String memo;
}

package app.rebow.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class CreateEntryRequest {

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "date must be YYYY-MM-DD")
    private String date;

    @NotEmpty(message = "petIds must not be empty")
    private List<Long> petIds;

    private String memo;
}

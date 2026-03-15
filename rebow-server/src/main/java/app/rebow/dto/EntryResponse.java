package app.rebow.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EntryResponse {
    private Long id;
    private String date;
    private List<Long> petIds;
    private String memo;
}

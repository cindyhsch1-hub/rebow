package app.rebow.controller;

import app.rebow.dto.CreateEntryRequest;
import app.rebow.dto.EntryResponse;
import app.rebow.dto.UpdateEntryRequest;
import app.rebow.service.EntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    // GET /api/entries/{date}?userId=1
    @GetMapping("/{date}")
    public EntryResponse getEntry(
            @RequestParam Long userId,
            @PathVariable String date
    ) {
        return entryService.getEntry(userId, date);
    }

    // POST /api/entries?userId=1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntryResponse createEntry(
            @RequestParam Long userId,
            @Valid @RequestBody CreateEntryRequest request
    ) {
        return entryService.createEntry(userId, request);
    }

    // PUT /api/entries/{date}?userId=1
    @PutMapping("/{date}")
    public EntryResponse updateEntry(
            @RequestParam Long userId,
            @PathVariable String date,
            @Valid @RequestBody UpdateEntryRequest request
    ) {
        return entryService.updateEntry(userId, date, request);
    }
}

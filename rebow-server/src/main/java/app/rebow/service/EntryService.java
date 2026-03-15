package app.rebow.service;

import app.rebow.dto.CreateEntryRequest;
import app.rebow.dto.EntryResponse;
import app.rebow.dto.UpdateEntryRequest;
import app.rebow.entity.*;
import app.rebow.exception.InvalidRequestException;
import app.rebow.repository.*;
import app.rebow.validation.EntryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;
    private final UserRepository  userRepository;
    private final EntryValidator  entryValidator;

    @Transactional(readOnly = true)
    public EntryResponse getEntry(Long userId, String dateStr) {
        LocalDate date = entryValidator.parseDate(dateStr);

        Entry entry = entryRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new InvalidRequestException("No entry found for this date"));

        return toResponse(entry);
    }

    @Transactional
    public EntryResponse createEntry(Long userId, CreateEntryRequest req) {
        LocalDate date = entryValidator.parseDate(req.getDate());
        entryValidator.validateDateNotDuplicated(userId, date);
        List<Pet> pets = entryValidator.validatePetOwnership(req.getPetIds(), userId);

        User user = userRepository.getReferenceById(userId);
        Entry entry = Entry.builder()
                .user(user)
                .date(date)
                .memo(req.getMemo() != null ? req.getMemo() : "")
                .build();
        entryRepository.save(entry);

        List<EntryPet> entryPets = pets.stream()
                .map(pet -> EntryPet.builder().entry(entry).pet(pet).build())
                .toList();
        entry.getEntryPets().addAll(entryPets);

        return toResponse(entry);
    }

    @Transactional
    public EntryResponse updateEntry(Long userId, String dateStr, UpdateEntryRequest req) {
        LocalDate date = entryValidator.parseDate(dateStr);

        Entry entry = entryRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new InvalidRequestException("No entry found for this date"));

        List<Pet> pets = entryValidator.validatePetOwnership(req.getPetIds(), userId);

        if (req.getMemo() != null) {
            entry.setMemo(req.getMemo());
        }

        entry.getEntryPets().clear();
        List<EntryPet> newEntryPets = pets.stream()
                .map(pet -> EntryPet.builder().entry(entry).pet(pet).build())
                .toList();
        entry.getEntryPets().addAll(newEntryPets);

        return toResponse(entry);
    }

    private EntryResponse toResponse(Entry entry) {
        List<Long> petIds = entry.getEntryPets().stream()
                .map(ep -> ep.getPet().getId())
                .toList();

        return EntryResponse.builder()
                .id(entry.getId())
                .date(entry.getDate().toString())
                .petIds(petIds)
                .memo(entry.getMemo())
                .build();
    }
}
package app.rebow.validation;

import app.rebow.entity.Pet;
import app.rebow.exception.ConflictException;
import app.rebow.exception.InvalidRequestException;
import app.rebow.repository.EntryRepository;
import app.rebow.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EntryValidator {

    private final EntryRepository entryRepository;
    private final PetRepository petRepository;

    public LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    public void validateDateNotDuplicated(Long userId, LocalDate date) {
        if (entryRepository.existsByUserIdAndDate(userId, date)) {
            throw new ConflictException("An entry already exists for this date");
        }
    }

    public List<Pet> validatePetOwnership(List<Long> petIds, Long userId) {
        List<Pet> pets = petRepository.findByIdsAndUserId(petIds, userId);
        if (pets.size() != petIds.size()) {
            throw new InvalidRequestException("One or more petIds are invalid");
        }
        return pets;
    }
}

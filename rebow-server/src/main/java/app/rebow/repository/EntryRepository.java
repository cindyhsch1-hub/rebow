package app.rebow.repository;

import app.rebow.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("""
        SELECT e FROM Entry e
        LEFT JOIN FETCH e.entryPets ep
        LEFT JOIN FETCH ep.pet
        WHERE e.user.id = :userId
          AND e.date = :date
        """)
    Optional<Entry> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date")   LocalDate date
    );

    boolean existsByUserIdAndDate(Long userId, LocalDate date);
}
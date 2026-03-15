package app.rebow.repository;

import app.rebow.entity.EntryPet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryPetRepository extends JpaRepository<EntryPet, Long> {}

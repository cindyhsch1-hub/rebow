package app.rebow.repository;

import app.rebow.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserId(Long userId);

    @Query("SELECT p FROM Pet p WHERE p.id IN :ids AND p.user.id = :userId")
    List<Pet> findByIdsAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);
}

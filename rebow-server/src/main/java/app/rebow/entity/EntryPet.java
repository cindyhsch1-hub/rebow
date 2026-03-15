package app.rebow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entry_pets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EntryPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}

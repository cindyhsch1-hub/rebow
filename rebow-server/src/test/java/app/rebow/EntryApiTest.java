package app.rebow;

import app.rebow.entity.Pet;
import app.rebow.entity.User;
import app.rebow.repository.EntryRepository;
import app.rebow.repository.PetRepository;
import app.rebow.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntryApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PetRepository petRepository;
    @Autowired EntryRepository entryRepository;

    static Long userId;
    static Long petId1;
    static Long petId2;

    @BeforeEach
    void setUp() {
        if (userId == null) {
            User user = User.builder()
                    .email("test@rebow.app")
                    .name("Test User")
                    .build();
            user = userRepository.save(user);
            userId = user.getId();

            Pet pet1 = Pet.builder().user(user).name("Buddy").breed("Golden Retriever").birthday(LocalDate.of(2020, 1, 1)).build();
            Pet pet2 = Pet.builder().user(user).name("Max").breed("Poodle").birthday(LocalDate.of(2021, 6, 15)).build();
            pet1 = petRepository.save(pet1);
            pet2 = petRepository.save(pet2);
            petId1 = pet1.getId();
            petId2 = pet2.getId();
        }
    }

    @Test
    @Order(1)
    void createEntry_success() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "date", "2026-03-15",
                "petIds", List.of(petId1, petId2),
                "memo", "Happy day with pets"
        ));

        mockMvc.perform(post("/api/entries")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value("2026-03-15"))
                .andExpect(jsonPath("$.petIds.length()").value(2))
                .andExpect(jsonPath("$.memo").value("Happy day with pets"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @Order(2)
    void createEntry_duplicateDate_returns409() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "date", "2026-03-15",
                "petIds", List.of(petId1)
        ));

        mockMvc.perform(post("/api/entries")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }

    @Test
    @Order(3)
    void getEntry_success() throws Exception {
        mockMvc.perform(get("/api/entries/2026-03-15")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-03-15"))
                .andExpect(jsonPath("$.petIds.length()").value(2))
                .andExpect(jsonPath("$.memo").value("Happy day with pets"));
    }

    @Test
    @Order(4)
    void getEntry_notFound_returns400() throws Exception {
        mockMvc.perform(get("/api/entries/2026-01-01")
                        .param("userId", userId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @Order(5)
    void updateEntry_success() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "petIds", List.of(petId1),
                "memo", "Updated memo"
        ));

        mockMvc.perform(put("/api/entries/2026-03-15")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petIds.length()").value(1))
                .andExpect(jsonPath("$.petIds[0]").value(petId1))
                .andExpect(jsonPath("$.memo").value("Updated memo"));
    }

    @Test
    @Order(6)
    void updateEntry_notFound_returns400() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "petIds", List.of(petId1)
        ));

        mockMvc.perform(put("/api/entries/2099-12-31")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void createEntry_invalidPetId_returns400() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "date", "2026-04-01",
                "petIds", List.of(99999L)
        ));

        mockMvc.perform(post("/api/entries")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @Order(8)
    void createEntry_missingDate_returns422() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "petIds", List.of(petId1)
        ));

        mockMvc.perform(post("/api/entries")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    @Order(9)
    void createEntry_emptyPetIds_returns422() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "date", "2026-05-01",
                "petIds", List.of()
        ));

        mockMvc.perform(post("/api/entries")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Order(10)
    void getEntry_invalidDateFormat_returns400() throws Exception {
        mockMvc.perform(get("/api/entries/not-a-date")
                        .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());
    }
}

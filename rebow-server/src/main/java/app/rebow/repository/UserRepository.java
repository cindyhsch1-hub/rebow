package app.rebow.repository;

import app.rebow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByAppleId(String appleId);
}

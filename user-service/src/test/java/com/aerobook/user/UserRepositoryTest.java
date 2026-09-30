package com.aerobook.user;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.geode.autoconfigure.DataJpaTestAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.aerobook.user.entity.Role;
import com.aerobook.user.entity.UserEntity;
import com.aerobook.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit 5 Data JPA test validating database queries and constraints on UserRepository.
 * Uses in-memory H2 database.
 */
@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:user_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail - should return user when email matches")
    void testFindByEmail_Success() {
        UserEntity user = new UserEntity(
                null,
                "Ravi",
                "Patel",
                "ravi.patel@example.com",
                "9876543212",
                LocalDate.of(1995, 9, 20),
                "Indian",
                Role.ROLE_USER
        );
        entityManager.persistAndFlush(user);

        Optional<UserEntity> found = userRepository.findByEmail("ravi.patel@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ravi");
        assertThat(found.get().getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("existsByEmail - should return true when email exists")
    void testExistsByEmail() {
        UserEntity user = new UserEntity(
                null,
                "Asha",
                "Khan",
                "asha.khan@example.com",
                "9876543213",
                LocalDate.of(1998, 4, 15),
                "Indian",
                Role.ROLE_USER
        );
        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmail("asha.khan@example.com");
        boolean notExists = userRepository.existsByEmail("unknown@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("existsByPhoneNumber - should return true when phone exists")
    void testExistsByPhoneNumber() {
        UserEntity user = new UserEntity(
                null,
                "Suresh",
                "Raina",
                "suresh@example.com",
                "9876543214",
                LocalDate.of(1990, 11, 27),
                "Indian",
                Role.ROLE_USER
        );
        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByPhoneNumber("9876543214");
        boolean notExists = userRepository.existsByPhoneNumber("0000000000");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}

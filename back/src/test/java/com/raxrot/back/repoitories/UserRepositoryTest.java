package com.raxrot.back.repoitories;

import com.raxrot.back.models.AppRole;
import com.raxrot.back.models.Role;
import com.raxrot.back.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = roleRepository.save(new Role(AppRole.ROLE_USER));

        User user = new User("test", "12345", "test@example.com");
        user.getRoles().add(userRole);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Find by username")
    void findByUsername() {
        Optional<User> user = userRepository.findByUsername("test");

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Check if username exists")
    void existsByUsername() {
        boolean exists = userRepository.existsByUsername("test");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Check if email exists")
    void existsByEmail() {
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }
}
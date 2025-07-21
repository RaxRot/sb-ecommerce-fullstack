package com.raxrot.back.repoitories;

import com.raxrot.back.models.AppRole;
import com.raxrot.back.models.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Find role by name")
    void findByRoleName() {
        Role role = new Role(AppRole.ROLE_ADMIN);
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByRoleName(AppRole.ROLE_ADMIN);

        assertThat(found).isPresent();
        assertThat(found.get().getRoleName()).isEqualTo(AppRole.ROLE_ADMIN);
    }
}
package com.luka.userauth.repository;

import com.luka.userauth.config.TestContainerDatabaseConfig;
import com.luka.userauth.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
public class RoleRepositoryTests extends TestContainerDatabaseConfig {

    @Autowired
    private RoleRepository roleRepository;

    private Role role;
    private Role roleSameData;

    @BeforeEach
    void setUp() {
        role = new Role(null, "TEST_ROLE_USER");
        roleSameData = new Role(null, "TEST_ROLE_USER");
    }

    @Test
    void shouldSaveRole(){

        Role dbRole = roleRepository.saveAndFlush(role);

        Assertions.assertNotNull(dbRole.getId());

        Optional<Role> dbRole2 = roleRepository.findById(dbRole.getId());

        Assertions.assertTrue(dbRole2.isPresent());
        Assertions.assertEquals(role.getName(), dbRole2.get().getName());

    }

    @Test
    void shouldNotSaveRoleSameName() {

        roleRepository.saveAndFlush(role);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

            roleRepository.saveAndFlush(roleSameData);

        });
    }

    @Test
    void shouldNotSaveRoleNullName() {

        role.setName(null);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

            roleRepository.saveAndFlush(role);

        });

    }

    @Test
    void shouldFindRoleByName() {

        Role savedRole = roleRepository.saveAndFlush(role);

        Optional<Role> dbRole = roleRepository.findByName("TEST_ROLE_USER");

        Assertions.assertTrue(dbRole.isPresent());
        Assertions.assertNotNull(dbRole.get().getId());
        Assertions.assertEquals(savedRole.getName(), dbRole.get().getName());
        Assertions.assertEquals(savedRole.getId(), dbRole.get().getId());

    }

    @Test
    void shouldReturnEmptyIfRoleNotFound() {

        Optional<Role> dbRole = roleRepository.findByName("SOME_UNKNOWN_ROLE_NAME");

        Assertions.assertTrue(dbRole.isEmpty());

    }

}

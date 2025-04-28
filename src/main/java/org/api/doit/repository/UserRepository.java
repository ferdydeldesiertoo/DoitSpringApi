package org.api.doit.repository;

import org.api.doit.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for accessing User entities in the database.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return an Optional containing the user if found, or empty if not
     */
    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    boolean existsByUsername(String username);
}

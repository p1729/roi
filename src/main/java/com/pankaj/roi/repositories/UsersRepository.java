package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByFbId(String fbId);
}

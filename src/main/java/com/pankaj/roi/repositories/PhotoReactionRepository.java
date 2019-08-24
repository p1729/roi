package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.PhotoReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoReactionRepository extends JpaRepository<PhotoReaction, Long> {
    Optional<PhotoReaction> findByFbId(String fbId);
}

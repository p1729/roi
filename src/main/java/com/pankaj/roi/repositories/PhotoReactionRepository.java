package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.PhotoReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoReactionRepository extends JpaRepository<PhotoReaction, Long> {
}

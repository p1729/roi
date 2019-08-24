package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByFbIdIn(List<String> fbIds);
    Optional<Photo> findByFbId(String fbId);
}
package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
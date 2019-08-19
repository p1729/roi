package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.PhotoAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
}

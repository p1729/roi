package com.pankaj.roi.repositories;

import com.pankaj.roi.entities.PhotoAlbum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
	List<PhotoAlbum> findByFbIdIn(List<String> fbIds);
	Optional<PhotoAlbum> findByFbId(String fbId);
}

package com.pankaj.roi.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photo_album")
public class PhotoAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fb_id", unique = true)
    private String fbId;

    private String name;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private Set<Photo> photoList = new HashSet<>();

    public void addPhoto(Photo photo) {
        photoList.add(photo);
        photo.setAlbum(this);
    }

    public void removePhoto(Photo photo) {
        photoList.remove(photo);
        photo.setAlbum(null);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PhotoAlbum that = (PhotoAlbum) o;
		return fbId.equals(that.fbId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fbId);
	}
}
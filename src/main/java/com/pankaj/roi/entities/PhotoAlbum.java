package com.pankaj.roi.entities;

import com.pankaj.roi.models.Album;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private List<Photo> photoList = new ArrayList<>();

    public void addPhoto(Photo photo) {
        photoList.add(photo);
        photo.setAlbum(this);
    }

    public void removePhoto(Photo photo) {
        photoList.remove(photo);
        photo.setAlbum(null);
    }

    

    public static PhotoAlbum of(Album a) {
        return Objects.isNull(a) ? null : PhotoAlbum.builder()
                .fbId(a.getId())
                .name(a.getName())
                .build();
    }

    public static List<PhotoAlbum> of(List<Photo> photos) {
        return photos.stream()
                .map(photo -> {
                    if (Objects.isNull(photo)) return null;
                    return photo.getAlbum();
                })
                .filter(photoAlbum -> !Objects.isNull(photoAlbum))
                .collect(Collectors.toList());
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
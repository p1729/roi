package com.pankaj.roi.entities;

import com.pankaj.roi.models.FBPhotoData;
import com.pankaj.roi.models.FBPhotos;
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
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fb_id", unique = true)
    private String fbId;

    @Column(name = "fb_link")
    private String fbLink;

    @Column(name = "image_link")
    private String imageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private PhotoAlbum album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoReaction> photoReactions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addReaction(PhotoReaction reaction) {
        photoReactions.add(reaction);
        reaction.setPhoto(this);
    }

    public void removeReaction(PhotoReaction reaction) {
        photoReactions.remove(reaction);
        reaction.setPhoto(null);
    }

    private static Photo of(FBPhotoData data, User user) {
        return Photo.builder()
                .fbId(data.getId())
                .fbLink(data.getLink())
                .imageLink(data.getPicture())
                .album(PhotoAlbum.of(data.getAlbum()))
                .user(user)
                .build();
    }

    public static List<Photo> of(FBPhotos fbPhotos, User user) {
        return fbPhotos.getData().stream().map(d -> Photo.of(d, user)).collect(Collectors.toList());
    }
}

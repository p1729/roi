package com.pankaj.roi.entities;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private PhotoAlbum album;

    @ToString.Exclude
    @ManyToMany(
            cascade = {CascadeType.MERGE}
    )
    @JoinTable(
            name= "user_tag_photo",
            joinColumns = @JoinColumn(name= "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhotoReaction> photoReactions = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
        user.getPhotos().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return fbId.equals(photo.fbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fbId);
    }

    public void setPhotoReactions(Set<PhotoReaction> reactions) {
        if(Objects.nonNull(reactions)) photoReactions.addAll(reactions);
    }

    public void addReaction(PhotoReaction reaction) {
        photoReactions.add(reaction);
        reaction.setPhoto(this);
    }

    public void addReactions(Set<PhotoReaction> reactions) {
        photoReactions.addAll(reactions);
        reactions.forEach(reaction -> reaction.setPhoto(this));
    }

    public void removeReaction(PhotoReaction reaction) {
        photoReactions.remove(reaction);
        reaction.setPhoto(null);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getPhotos().remove(this);
    }
}

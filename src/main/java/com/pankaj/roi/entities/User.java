package com.pankaj.roi.entities;

import com.pankaj.roi.enums.Gender;
import com.pankaj.roi.models.FBUser;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fb_id", unique = true)
    private String fbId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    @Column(name = "created_on", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Column(name = "profile_pic_url")
    private String profilePicURL;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Photo> photoList = new ArrayList<>();

    @PrePersist
    public void perPersist() {
        createdOn = new Date();
    }

    public void addPhoto(Photo photo) {
        photoList.add(photo);
        photo.setUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void removePhoto(Photo photo) {
        photoList.remove(photo);
        photo.setUser(null);
    }

    public static User of(FBUser fbUser) {
        return User.builder()
                .fbId(fbUser.getId())
                .email(fbUser.getEmail())
                .gender(Gender.valueOf(fbUser.getGender().toUpperCase()))
                .name(fbUser.getName())
                .profilePicURL(fbUser.getPicture().getData().getUrl())
                .build();
    }
}

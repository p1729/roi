package com.pankaj.roi.entities;

import com.pankaj.roi.enums.Gender;
import com.pankaj.roi.models.FBUser;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @ManyToMany(
            mappedBy = "users"
    )
    private Set<Photo> photos = new HashSet<>();

    @PrePersist
    public void perPersist() {
        createdOn = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return fbId.equals(user.fbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fbId);
    }

    public static User of(FBUser fbUser) {
        return User.builder()
                .fbId(fbUser.getId())
                .email(fbUser.getEmail())
                .gender(Gender.valueOf(fbUser.getGender().toUpperCase()))
                .name(fbUser.getName())
                .photos(new HashSet<>())
                .profilePicURL(fbUser.getPicture().getData().getUrl())
                .build();
    }
}

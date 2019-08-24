package com.pankaj.roi.entities;

import com.pankaj.roi.enums.ReactionType;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fb_id")
    private String fbId;

    @Enumerated(EnumType.STRING)
    private ReactionType type;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoReaction that = (PhotoReaction) o;
        return fbId.equals(that.fbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fbId);
    }




}

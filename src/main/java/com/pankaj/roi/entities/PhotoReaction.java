package com.pankaj.roi.entities;

import com.pankaj.roi.enums.ReactionType;
import com.pankaj.roi.models.Reaction;
import com.pankaj.roi.models.ReactionsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoReaction that = (PhotoReaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    
    public static List<PhotoReaction> of(ReactionsData reactions) {
        if(Objects.isNull(reactions)) return Collections.emptyList();
        return reactions.getData().stream().map(r -> PhotoReaction.of(r)).collect(Collectors.toList());
    }

    public static PhotoReaction of(Reaction r) {
        return PhotoReaction.builder()
                .fbId(r.getId())
                .type(ReactionType.valueOf(r.getType().toUpperCase()))
                .build();
    }

    public static List<PhotoReaction> of(List<Photo> photos) {
        return photos.stream().filter(Objects::nonNull)
                .filter(p -> Objects.nonNull(p.getPhotoReactions()))
                .filter(p -> !p.getPhotoReactions().isEmpty())
                .flatMap(p -> p.getPhotoReactions().stream())
                .collect(Collectors.toList());
    }
}

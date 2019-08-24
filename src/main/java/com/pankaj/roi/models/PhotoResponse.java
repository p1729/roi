package com.pankaj.roi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class PhotoResponse {

    private String link;

    @JsonProperty("fb_link")
    private String fbLink;

    @JsonProperty("album_name")
    private String albumName;

    @Setter(AccessLevel.NONE)
    private List<PhotoReactionResponse> reactions = new ArrayList<>();

    public void setReactions(List<PhotoReactionResponse> reactions) {
        if(Objects.nonNull(reactions))
            this.reactions.addAll(reactions);
    }
}

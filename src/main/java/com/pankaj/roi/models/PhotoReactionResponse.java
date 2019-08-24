package com.pankaj.roi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoReactionResponse {
    private String type;

    @JsonProperty("total_count")
    private String totalCount;
}

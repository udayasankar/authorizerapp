package com.sample.anamoly.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountResponse {

    @JsonProperty("available-limit")
    private String availableLimit;

    @JsonProperty("active-card")
    private String activeCard;

}
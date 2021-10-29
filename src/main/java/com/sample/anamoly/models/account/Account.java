package com.sample.anamoly.models.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {

    @JsonProperty("available-limit")
    private int availableLimit;

    @JsonProperty("active-card")
    private boolean activeCard;

}
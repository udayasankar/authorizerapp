package com.sample.anamoly.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AccountCreationResponse {

    @JsonProperty("account")
    private AccountResponse accountResponse;

    @JsonProperty("violations")
    private List<String> violations;

}
package com.sample.anamoly.models.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountCreation {

    @JsonProperty("account")
    private Account account;

    public Account getAccount() {
        return account;
    }


    public void setAccount(Account account) {
        this.account = account;
    }

}
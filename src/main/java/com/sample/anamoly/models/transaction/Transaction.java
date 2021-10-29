package com.sample.anamoly.models.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("time")
    private String time;

    public int getAmount() {
        return amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getTime() {
        return time;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
package com.sample.anamoly.models.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionAuthorization {

    @JsonProperty("transaction")
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}
package com.sample.anamoly.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sample.anamoly.models.account.AccountCreation;
import com.sample.anamoly.models.response.AccountCreationResponse;
import com.sample.anamoly.models.response.AccountResponse;
import com.sample.anamoly.models.transaction.TransactionAuthorization;
import com.sample.anamoly.utils.AuthorizerAppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BusinessRuleService {
    /**
     * This files will evaluate the rules associated for the events considering five violations
     * as per requirement. There is account and transaction we can add new rules accordingly.
     */
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private Cache<String, String> cacheTransaction;

    public void rulesCheckPutCacheAccount(AccountCreation accountCreation) {

        if (null != accountCreation &&
                null == cacheTransaction.getIfPresent("accountlimit")) {
            cacheTransaction.put("accountlimit",
                    String.valueOf(accountCreation.getAccount().getAvailableLimit()));
            cacheTransaction.put("accountactive",
                    String.valueOf(accountCreation.getAccount().isActiveCard()));
        }
    }

    public void rulesCheckPutCacheTransaction(TransactionAuthorization transactionAuthorization) {
        if (null != transactionAuthorization &&
                null == cacheTransaction.getIfPresent("transactionmerchant")) {
            cacheTransaction.put("transactiontime",
                    LocalDateTime.now().toString());
            cacheTransaction.put("transactionmerchant",
                    transactionAuthorization.getTransaction().getMerchant());
            cacheTransaction.put("transactionamount",
                    String.valueOf(transactionAuthorization.getTransaction().getAmount()));
        }

    }

    public void ruleEvaluationCheckAccount(AccountCreation accountCreation){
        try {
            if (cacheTransaction.getIfPresent("accountactive") == null) {
                AccountCreationResponse accountCreationResponse = rulesAccount(accountCreation);
                log.info(objectMapper.writeValueAsString(accountCreationResponse));
                return;
            }

            if (cacheTransaction.getIfPresent("accountactive") != null) {
                AccountCreationResponse accountCreationResponse = rulesAccount(accountCreation);
                log.info(objectMapper.writeValueAsString(accountCreationResponse));
                return;
            }
        }catch(Exception ex)
        {
            log.error("Json parse exception", ex);
        }

    }

    public void ruleEvaluationCheckTransaction(TransactionAuthorization transactionAuthorization) {
        try {
            if (null != transactionAuthorization &&
                    transactionAuthorization.getTransaction().getAmount() > 0) {
                AccountCreationResponse accountCreationResponse = rulesTransaction(transactionAuthorization);
                log.info(objectMapper.writeValueAsString(accountCreationResponse));
                return;
            }
        }catch(Exception ex)
        {
            log.error("Json parse exception", ex);
        }
    }

    public AccountCreationResponse rulesAccount(AccountCreation accountCreation) {
        List<String> violationsEvents = new ArrayList<>();
        if (cacheTransaction.getIfPresent("accountactive") == null) {
            violationsEvents.add(AuthorizerAppConstants.ACCOUNT_NOTINITIALIZED);
            AccountCreationResponse accountCreationResponse = responseCreation(violationsEvents);
            return accountCreationResponse;
        }
        if (cacheTransaction.getIfPresent("accountactive") != null) {
            Boolean activeValue = Boolean.parseBoolean(cacheTransaction.getIfPresent("accountactive"));
            if (!activeValue)
                violationsEvents.add(AuthorizerAppConstants.CARD_NOTACTIVE);
            AccountCreationResponse accountCreationResponse = responseCreation(violationsEvents);
            return accountCreationResponse;
        }
        return null;
    }

    public AccountCreationResponse rulesTransaction(TransactionAuthorization transactionAuthorization) {
        List<String> violationsEvents = new ArrayList<>();
        if (null != cacheTransaction.getIfPresent("accountlimit")) {
            int accountLimitValue = Integer.parseInt(cacheTransaction.getIfPresent("accountlimit"));
            int totalSum = 0;
            if (accountLimitValue > 0) {
                int transactionAmount = transactionAuthorization.getTransaction().getAmount();
                totalSum = accountLimitValue - transactionAmount;
                if (totalSum < 0)
                    violationsEvents.add(AuthorizerAppConstants.INSUFFICIENT_LIMIT);
            }
            if (totalSum > 0)
                cacheTransaction.put("accountlimit", String.valueOf(totalSum));
        }
        if (null != cacheTransaction.getIfPresent("transactiontime")) {
            long evalTime = evaluateTime(cacheTransaction.getIfPresent("transactiontime"),
                    LocalDateTime.now().toString());
            if (evalTime < 2) {
                violationsEvents.add(AuthorizerAppConstants.HIGH_FREQUENCY);
            }
            cacheTransaction.put("transactiontime", LocalDateTime.now().toString());
        }
        if (null != cacheTransaction.getIfPresent("transactionmerchant") &&
                null != cacheTransaction.getIfPresent("transactionamount")) {
            if (cacheTransaction.getIfPresent("transactionmerchant")
                    .equals(transactionAuthorization.getTransaction().getMerchant()) &&
                    cacheTransaction.getIfPresent("transactionamount")
                            .equals(String.valueOf(transactionAuthorization.getTransaction().getAmount()))) {
                if (violationsEvents.contains(AuthorizerAppConstants.HIGH_FREQUENCY))
                    violationsEvents.add(AuthorizerAppConstants.DOUBLED_TRANSACTION);
            }
            cacheTransaction.put("transactionmerchant",
                    transactionAuthorization.getTransaction().getMerchant());
            cacheTransaction.put("transactionamount",
                    String.valueOf(transactionAuthorization.getTransaction().getAmount()));
        }
        if (violationsEvents.size() > 0) {
            AccountCreationResponse accountCreationResponse = responseCreation(violationsEvents);
            return accountCreationResponse;
        }
        return null;
    }

    public long evaluateTime(String endDate, String transactionTime) {
        LocalDateTime firstDate = LocalDateTime.parse(endDate);
        LocalDateTime secondDate = LocalDateTime.parse(transactionTime);
        long differenceInMinutes = ChronoUnit.MINUTES.between(firstDate, secondDate);
        return differenceInMinutes;
    }

    public AccountCreationResponse responseCreation(List<String> violationsEvents) {
        String cacheKey = cacheTransaction.getIfPresent("accountlimit");
        String cacheActiveKey = cacheTransaction.getIfPresent("accountactive");
        AccountResponse accountResponse = AccountResponse.builder()
                .availableLimit(cacheKey == null ? "novalue" : cacheKey.toString())
                .activeCard(cacheActiveKey)
                .build();
        AccountCreationResponse accountCreationResponse = AccountCreationResponse.builder()
                .accountResponse(accountResponse)
                .violations(violationsEvents)
                .build();
        return accountCreationResponse;
    }
}

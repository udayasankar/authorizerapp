package com.sample.anamoly.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sample.anamoly.models.account.AccountCreation;
import com.sample.anamoly.models.transaction.TransactionAuthorization;
import com.sample.anamoly.utils.AuthorizerAppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleUsageService {
    /**
     * Read events as streams and process the rule logics.
     */

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BusinessRuleService businessRuleService;

    @Autowired
    private Cache<String, String> cacheTransaction;

    @Async
    public String sampleUsage(List<String> sampleArg) {
        sampleArg.stream().map(bankEvents ->
        {
            AccountCreation accountCreation = null;
            TransactionAuthorization transactionAuthorization = null;
            try {
                if (bankEvents.contains(AuthorizerAppConstants.ACCOUNT)) {
                    accountCreation = objectMapper.
                            readValue(bankEvents, AccountCreation.class);
                    System.out.println(sampleArg);
                    if (null == cacheTransaction.getIfPresent("accountactive")) {
                        businessRuleService.rulesCheckPutCacheAccount(accountCreation);
                        System.out.println(cacheTransaction.getIfPresent("accountlimit"));
                        businessRuleService.ruleEvaluationCheckAccount(accountCreation);
                    }
                }
                if (bankEvents.contains(AuthorizerAppConstants.TRANSACTION)) {
                    transactionAuthorization = objectMapper.
                            readValue(bankEvents, TransactionAuthorization.class);
                    System.out.println(sampleArg);
                    if (null == cacheTransaction.getIfPresent("transactionmerchant")) {
                        businessRuleService.rulesCheckPutCacheTransaction(transactionAuthorization);
                        businessRuleService.ruleEvaluationCheckTransaction(transactionAuthorization);
                    } else
                        businessRuleService.ruleEvaluationCheckTransaction(transactionAuthorization);

                }
            } catch (Exception ex) {
                log.error("Exception in json parsing", ex);
            }
            return null;
        }).collect(Collectors.toList());
        return "Processed";
    }

}


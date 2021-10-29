package com.sample.anamoly.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sample.anamoly.models.account.Account;
import com.sample.anamoly.models.account.AccountCreation;
import com.sample.anamoly.models.response.AccountCreationResponse;
import com.sample.anamoly.models.transaction.Transaction;
import com.sample.anamoly.models.transaction.TransactionAuthorization;
import com.sample.anamoly.utils.AuthorizerAppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class BusinessRuleServiceTest {
    @InjectMocks
    BusinessRuleService businessRuleService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    private Cache<String, String> cacheTransaction;

    List<String> messageEvent;
    AccountCreation accountCreation;
    Account account;
    TransactionAuthorization transactionAuthorization;
    Transaction transaction;
    List<String> violationsEvents;

    @BeforeEach
    public void init() {
        accountCreation = new AccountCreation();
        account = new Account();
        account.setActiveCard(true);
        account.setAvailableLimit(100);
        accountCreation.setAccount(account);

        transactionAuthorization = new TransactionAuthorization();
        transaction = new Transaction();

        transaction.setAmount(100);
        transaction.setMerchant("Burger King");
        transaction.setTime("2019-02-13T10:00:00.000Z");
        transactionAuthorization.setTransaction(transaction);

       violationsEvents = new ArrayList<>();

        cacheTransaction.put("accountlimit",
                String.valueOf(accountCreation.getAccount().getAvailableLimit()));
        cacheTransaction.put("accountactive",
                String.valueOf(accountCreation.getAccount().isActiveCard()));
        cacheTransaction.put("transactiontime",
                transactionAuthorization.getTransaction().getTime());
        cacheTransaction.put("transactionmerchant",
                transactionAuthorization.getTransaction().getMerchant());
        cacheTransaction.put("transactionamount",
                String.valueOf(transactionAuthorization.getTransaction().getAmount()));
    }

    @Test
    public void rulesCheckPutCacheAccountTest() {
        Mockito.when(cacheTransaction.getIfPresent("accountlimit")).thenReturn("100");
        Mockito.when(cacheTransaction.getIfPresent("accountactive")).thenReturn("true");
        assertEquals(cacheTransaction.getIfPresent("accountlimit"),"100");
        assertEquals(cacheTransaction.getIfPresent("accountactive"),"true");
    }

    @Test
    public void rulesCheckPutCacheTransactionTest() {
        Mockito.when(cacheTransaction.getIfPresent("transactiontime")).thenReturn("2019-02-13T10:00:00.000Z");
        Mockito.when(cacheTransaction.getIfPresent("transactionmerchant")).thenReturn("Burger King");
        Mockito.when(cacheTransaction.getIfPresent("transactionamount")).thenReturn("20");

        assertEquals(cacheTransaction.getIfPresent("transactiontime"),"2019-02-13T10:00:00.000Z");
        assertEquals(cacheTransaction.getIfPresent("transactionmerchant"),"Burger King");
        assertEquals(cacheTransaction.getIfPresent("transactionamount"),"20");
    }

    @Test
    public void ruleEvaluationCheckAccountTest() {
        Mockito.when(cacheTransaction.getIfPresent("accountlimit")).thenReturn(null);
        Mockito.when(cacheTransaction.getIfPresent("accountactive")).thenReturn(null);
        businessRuleService.ruleEvaluationCheckAccount(accountCreation);
    }

    @Test
    public void ruleEvaluationCheckTransactionTest() {
        businessRuleService.ruleEvaluationCheckTransaction(transactionAuthorization);
    }

    @Test
    public void rulesAccountTest() {
        Mockito.when(cacheTransaction.getIfPresent("accountactive")).thenReturn(String.valueOf("false"));
        AccountCreationResponse accountCreationResponse = businessRuleService.rulesAccount(accountCreation);
        Assertions.assertEquals(accountCreationResponse.getViolations().get(0), AuthorizerAppConstants.CARD_NOTACTIVE);
    }

    @Test
    public void rulesAccountAccountNotactiveTest() {
        Mockito.when(cacheTransaction.getIfPresent("accountactive")).thenReturn(null);
        AccountCreationResponse accountCreationResponse = businessRuleService.rulesAccount(accountCreation);
        assertEquals(accountCreationResponse.getViolations().get(0), AuthorizerAppConstants.ACCOUNT_NOTINITIALIZED);
    }

    @Test
    public void ruleEvaluationCheckTransactionAccountLimitTest() {
        Mockito.when(cacheTransaction.getIfPresent("accountlimit")).thenReturn("90");
        AccountCreationResponse accountCreationResponse = businessRuleService.rulesTransaction(transactionAuthorization);
        assertEquals(accountCreationResponse.getViolations().get(0), AuthorizerAppConstants.INSUFFICIENT_LIMIT);
    }

    @Test
    public void ruleEvaluationCheckTransactionHighFrequencyTest() {
        Mockito.when(cacheTransaction.getIfPresent("transactiontime")).thenReturn(LocalDateTime.now().toString());
        AccountCreationResponse accountCreationResponse = businessRuleService.rulesTransaction(transactionAuthorization);
        assertEquals(accountCreationResponse.getViolations().get(0), AuthorizerAppConstants.HIGH_FREQUENCY);
    }

    @Test
    public void ruleEvaluationDoubleTransactionTest() {
        Mockito.when(cacheTransaction.getIfPresent("transactionmerchant")).thenReturn("Burger King");
        Mockito.when(cacheTransaction.getIfPresent("transactiontime")).thenReturn(LocalDateTime.now().toString());
        Mockito.when(cacheTransaction.getIfPresent("transactionamount")).thenReturn("100");
        AccountCreationResponse accountCreationResponse = businessRuleService.rulesTransaction(transactionAuthorization);
        assertEquals(accountCreationResponse.getViolations().get(1), AuthorizerAppConstants.DOUBLED_TRANSACTION);
        assertEquals(accountCreationResponse.getViolations().get(0), AuthorizerAppConstants.HIGH_FREQUENCY);
    }

}

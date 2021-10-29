package com.sample.anamoly.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class SampleUsageServiceTest {

    @InjectMocks
    SampleUsageService sampleUsageService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    BusinessRuleService businessRuleService;

    @Mock
    private Cache<String, String> cacheTransaction;

    List<String> messageEvent;

    @BeforeEach
    public void init() {
        messageEvent = new ArrayList<>();
        messageEvent.add("{\"account\": {\"active-card\": true, \"available-limit\": 100}}");
        messageEvent.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}");

    }

    @Test
    public void sampleUsage() {
        SampleUsageService sampleUsageService = mock(SampleUsageService.class);
        Mockito.doReturn("Processed").when(sampleUsageService).sampleUsage(messageEvent);
        assertEquals(sampleUsageService.sampleUsage(messageEvent), "Processed");
    }
}

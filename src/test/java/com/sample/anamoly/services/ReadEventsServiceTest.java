package com.sample.anamoly.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class ReadEventsServiceTest {

    @InjectMocks
    ReadEventsService readEventsService;

    @BeforeEach
    public void init() {

    }

    @Test
    public void readFileTest() {
        assertEquals(readEventsService.readFile("batchfiles/operations.txt").size(),3);
    }

}

package com.sample.anamoly;

import com.sample.anamoly.services.ReadEventsService;
import com.sample.anamoly.services.SampleUsageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Slf4j
@Profile("!test")
public class AuthorizerappApplication implements CommandLineRunner {

    @Autowired
    ReadEventsService readEventsService;

    @Autowired
    SampleUsageService sampleUsageService;

    public static void main(String[] args) {

        SpringApplication.run(AuthorizerappApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("inside args testing");
        sampleUsageService.sampleUsage(readEventsService.readFile(args[0]));
    }

}

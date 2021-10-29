package com.sample.anamoly.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReadEventsService {
    /**
     *
     * Events are read from files and passed as streams and out will be dislayed in logs as mentioned
     * in Readme.md
     *
     */
    public List<String> readFile(String filePath) {
        List<String> eventList=null;
        if(null != filePath) {
            eventList = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    eventList.add(line);
                }

            } catch (IOException e) {
                log.error("File read exception");
            }
        }
        else
        {
            log.info("Please enter the file path and file name");
        }
        return eventList;
    }
}

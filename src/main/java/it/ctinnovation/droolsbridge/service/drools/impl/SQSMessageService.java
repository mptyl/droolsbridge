package it.ctinnovation.droolsbridge.service.drools.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.model.DAsset;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import it.ctinnovation.droolsbridge.service.drools.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQSMessageService implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SQSFactFeeder sqsFactFeeder;

    @Autowired
    SQSQueueManager awsQueueManager;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void start() {
        sqsFactFeeder.startFeedingFacts();
    }

    @Override
    public void stop() {
        sqsFactFeeder.stop();
    }

    @Override
    public  String getName(){
        return "SQS";
    }

    @Override
    public  String getDescr(){
        return "AWS Message Service";
    }

    @Override
    public void sendMessage(DAsset dasset) throws JsonProcessingException {
        String output=objectMapper.writeValueAsString(dasset);
        logger.info("Inviato messaggio\n{}",output);
        awsQueueManager.sendOutMessage(output);
    }
}

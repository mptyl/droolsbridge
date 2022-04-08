package it.ctinnovation.droolsbridge.service.drools.impl;

import it.ctinnovation.droolsbridge.model.Person;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import it.ctinnovation.droolsbridge.service.drools.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Profile("prod")
public class SQSMessageService implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SQSFactFeeder sqsFactFeeder;

    @Autowired
    SQSQueueManager awsQueueManager;

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
    public void sendMessage(Person person) {
        String outMessage = String.format("%s %s può ottenere la carta sconto FS.", person.getName(), person.getSurName());
        awsQueueManager.sendOutMessage(outMessage);
        logger.info(outMessage);

        //logger.info("{} {} può ottenere la carta sconto FS.", person.getName(), person.getSurName());
    }
}

package it.ctinnovation.droolsbridge.service.drools.impl;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.ctinnovation.droolsbridge.model.Person;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import it.ctinnovation.droolsbridge.service.drools.DroolsService;
import it.ctinnovation.droolsbridge.service.drools.FactFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Profile("prod")
public class SQSFactFeeder implements FactFeeder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // package contenente i model da mettere nella working memory
    public static final String MODEL_PACKAGE = "it.ctinnovation.droolsbridge.model.";

    // TODO - Flag che indica ...
    private volatile AtomicBoolean runFeeder = new AtomicBoolean(true);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    @Lazy
    DroolsService droolsService;

    @Autowired
    SQSQueueManager awsQueueManager;
    // values injected from config. file
    @Value("${sqs-feeder.feeder-delay}")
    private Integer feederDelay;

    @Value("${sqs-feeder.show-facts}")
    private Boolean showFacts;

    @Override
    public void startFeedingFacts() {
        logger.info("Starting SQS Fact fedeer ...");
        Map<Class, Integer> countMap = new HashMap<>();
        Integer total = 0;
        runFeeder.set(true);

        while(true) {
            logger.info("Try to read message");
            List<Message> messages = awsQueueManager.receiveInMessage();
            for(Message message: messages){
                String jsonMsg=message.getBody();

                // TODO - determinazione della classe sulla base della root del messaggio
                Class clazz = Person.class;
                ObjectReader objReader = objectMapper.readerFor(clazz);

                // TODO fare corretto try-catch
                // TODO rendere agnostico rispetto alla classe
                try  {
                    Object fact= objReader.readValue(jsonMsg);
                    if (showFacts) {
                        logger.info("New fact: {}", fact.toString());
                    }
                    droolsService.addToSession(fact);
                    if (feederDelay > 0) {
                        Thread.sleep(feederDelay);
                    }
                } catch (InterruptedException ex) {
                    logger.info(("Interrupted !"));
                } catch (IllegalArgumentException iae) {
                    logger.error("Unknown Fact type -  {}", iae.getMessage());
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void stop() {
        runFeeder.set(false);
        logger.info("SQSFactFeeder stopped");
    }

    // TODO - cambiare il modo di fare la find class basandola sulla root dell'oggetto
    private Class findClassOf(Path path) throws IllegalArgumentException {
        try {
            String className = MODEL_PACKAGE + path.getFileName().toString().split("\\.")[0].split("_")[0];
            logger.debug("The class name of {} is {}", path, className);
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Unable to desume the class of the Json stream " + path.getFileName());
        }
    }
}

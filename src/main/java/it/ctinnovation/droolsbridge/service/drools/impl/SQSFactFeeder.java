package it.ctinnovation.droolsbridge.service.drools.impl;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.ctinnovation.droolsbridge.config.MeasurementProperties;
import it.ctinnovation.droolsbridge.model.EventAsset;
import it.ctinnovation.droolsbridge.model.Measurement;
import it.ctinnovation.droolsbridge.model.TheaterEvent;
import it.ctinnovation.droolsbridge.service.SetupService;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import it.ctinnovation.droolsbridge.service.drools.DroolsService;
import it.ctinnovation.droolsbridge.service.drools.FactFeeder;
import it.ctinnovation.droolsbridge.util.MeasurementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SQSFactFeeder implements FactFeeder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // package contenente i model da mettere nella working memory
    public static final String MODEL_PACKAGE = "it.ctinnovation.droolsbridge.model.";

    // TODO - Flag che indica ...
    private volatile AtomicBoolean runFeeder = new AtomicBoolean(true);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SetupService setupService;

    @Autowired
    @Lazy
    DroolsService droolsService;

    @Autowired
    SQSQueueManager awsQueueManager;
    // values injected from config. file

    @Autowired
    MeasurementProperties measurementProperties;

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

                //////////////////  Messaggio ricevuto da Theater
                Class clazz = EventAsset.class;
                ObjectReader objReader = objectMapper.readerFor(clazz);

                try  {
                    EventAsset eventAsset= objReader.readValue(jsonMsg);
                    // completa i Measurement sulla base della mappatura adottata
                    MeasurementMapper.remapMeasurement(eventAsset,measurementProperties);

                    ////////////////// Inserimento nella working memory di Drools di un TheaterEvent per ogni misura
                    for(Measurement m:eventAsset.getPayload()){
                        TheaterEvent te = new TheaterEvent(eventAsset.getPlacemarkId(),eventAsset.getPosition(),eventAsset.getStatus(),eventAsset.getTimestamp(),eventAsset.getAttribute(),m);
                        droolsService.addToSession(te);
                        if (showFacts) {
                            logger.info("Inserito TheaterEvent {}",te);
                        }
                    }
                    ///////////////// Introduci un delay di 1 secondo
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

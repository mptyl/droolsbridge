package it.ctinnovation.droolsbridge;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.model.*;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Profile("prod")
public class AwsManagerTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AwsManagerTest.class);

    @Autowired
    SQSQueueManager awsQueueManager;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testReceiveInMessage(){
        List<Message> messages= awsQueueManager.receiveInMessage();
        if(messages.isEmpty())
            log.info("Lista messaggi vuota");
        else
        for(Message message:messages){
            log.info ("Messaggio: {}",message.getBody());
            awsQueueManager.deleteInMessage(message);
        }
    }

    @Test
    public void testSendInMessage(){
        SendMessageResult message=awsQueueManager.sendInMessage("Messaggio in uscita da Spring 3");
        log.info("Inviato messaggio "+ "Messaggio in uscita da Spring 3");
    }

    @Test
    public void testAssetCreation(){
        Position position=new Position(10.10f,20.20f);
        Attribute attribute1= new Attribute("placeId01","attr01","descrAttr01", AttributeType.CO2,"parti", AttributeValueType.INT,"10");
        Attribute attribute2= new Attribute("placeId02","attr02","descrAttr02", AttributeType.PM10,"partipm10", AttributeValueType.INT,"20");
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(attribute1);
        attributes.add(attribute2);
        Asset asset= new Asset();
        asset.setPlacemarkId("placeIdAAA");
        asset.setStatus(Status.NORMAL);
        asset.setPosition(position);
        asset.setTimestamp(new Date().toInstant());
        asset.setAttributes(attributes);

        try {
            log.info(objectMapper.writeValueAsString(asset));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}

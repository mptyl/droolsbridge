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

import java.util.List;

@SpringBootTest
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

}

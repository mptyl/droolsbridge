package it.ctinnovation.droolsbridge;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;
import it.ctinnovation.droolsbridge.service.AWSQueueManager;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AwsManagerTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AwsManagerTest.class);

    @Autowired
    AWSQueueManager awsQueueManager;

    @Test
    public void testConnection(){
        AmazonSQSClient amazonSQS = awsQueueManager.getInConsumerClient();
    }

    @Test
    public void testReceiveInMessage(){
        ReceiveMessageResult messages=awsQueueManager.receiveInMessage();
        if(messages.getMessages().isEmpty())
            log.info("Lista messaggi vuota");
        else
            for(Message message:messages.getMessages()){
                log.info("Body del message: {}",message.getBody());
                awsQueueManager.deleteInMessage(message);
            }
    }

    @Test
    public void testSendInMessage(){
        SendMessageResult message=awsQueueManager.sendInMessage("Messaggio in uscita da Spring");
        log.info("Inviato messaggio");
    }

}

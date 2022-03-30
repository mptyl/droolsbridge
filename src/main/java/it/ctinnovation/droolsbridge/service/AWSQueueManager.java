package it.ctinnovation.droolsbridge.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AWSQueueManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AWSQueueManager.class);

    @Autowired
    AWSCredentialsProvider inConsumer;

    @Autowired
    AWSCredentialsProvider inProducer;

    @Autowired
    AWSCredentialsProvider outProducer;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${sqs.queues.in.url}")
    private String inUrl;

    @Value("${sqs.queues.out.url}")
    private String outUrl;

    @Value("${sqs.queues.in.waitTimeSeconds}")
    private int waitTimeSeconds;

    @Value("${sqs.queues.in.maxNumberMessages}")
    private int maxNumberMessages;

    private final String QUEUE_NAME = "drllsCTIQueue";

    @Autowired
    private AmazonSQSClient inConsumerClient;

    @Autowired
    private AmazonSQSClient inProducerClient;

    @Autowired
    private AmazonSQSClient outProducerClient;

    public List<Message> receiveInMessage(){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(inUrl)
                .withWaitTimeSeconds(waitTimeSeconds)
                .withMaxNumberOfMessages(maxNumberMessages);
        List<Message> sqsMessages = inConsumerClient.receiveMessage(receiveMessageRequest).getMessages();
        if(sqsMessages.isEmpty())
            log.debug("Lista messaggi vuota");
        else
            for(Message message:sqsMessages) {
                log.debug("Messaggio: {}", message.getBody());
                deleteInMessage(message);
            }
        return sqsMessages;
    }

    public SendMessageResult sendInMessage(String messageBody){
        UUID uuid= UUID.randomUUID();
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl(inUrl)
                .withMessageBody(messageBody)
                .withMessageGroupId("DroolsGroup01")
                .withMessageDeduplicationId(uuid.toString());
        return inProducerClient.sendMessage(smr);
    }

    public SendMessageResult sendOutMessage(String messageBody){
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl(outUrl)
                .withMessageBody(messageBody)
                .withMessageGroupId("MPGroup")
                .withMessageDeduplicationId("DupId");
        return outProducerClient.sendMessage(smr);
    }

    public void deleteInMessage(Message message) {
        inConsumerClient.deleteMessage(inUrl,message.getReceiptHandle());
    }
}


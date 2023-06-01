package it.ctinnovation.droolsbridge.service.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SQSQueueManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQSQueueManager.class);

    @Autowired
    AWSCredentialsProvider inConsumer;

    @Autowired
    AWSCredentialsProvider outConsumer;

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
    private AmazonSQSClient outConsumerClient;

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
                .withMessageGroupId("DroolsGroupIn")
                .withMessageDeduplicationId(uuid.toString());
        return inProducerClient.sendMessage(smr);
    }

    public SendMessageResult sendOutMessage(String messageBody){
        UUID uuid= UUID.randomUUID();
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl(outUrl)
                .withMessageBody(messageBody)
                .withMessageGroupId("DroolsGroupOut")
                .withMessageDeduplicationId(uuid.toString());
        return outProducerClient.sendMessage(smr);
    }

    public void deleteInMessage(Message message) {
        inConsumerClient.deleteMessage(inUrl,message.getReceiptHandle());
    }

    public void deleteOutMessage(Message message) {
        outConsumerClient.deleteMessage(outUrl,message.getReceiptHandle());
    }

    public List<Message> receiveOutMessage() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(outUrl)
                .withWaitTimeSeconds(waitTimeSeconds)
                .withMaxNumberOfMessages(maxNumberMessages);
        List<Message> sqsMessages = outConsumerClient.receiveMessage(receiveMessageRequest).getMessages();
        if(sqsMessages.isEmpty())
            log.debug("Lista messaggi OUT vuota");
        else
            for(Message message:sqsMessages) {
                log.debug("Messaggio OUT:\n{}", message.getBody());
                deleteOutMessage(message);
            }
        return sqsMessages;
    }
}


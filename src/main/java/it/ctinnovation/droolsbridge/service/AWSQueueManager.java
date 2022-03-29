package it.ctinnovation.droolsbridge.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private final String QUEUE_NAME = "drllsCTIQueue";

    @Autowired
    private AmazonSQSClient inConsumerClient;

    @Autowired
    private AmazonSQSClient inProducerClient;

    @Autowired
    private AmazonSQSClient outProducerClient;

    public ReceiveMessageResult receiveInMessage(){
        return inConsumerClient.receiveMessage(inUrl);
    }

    public SendMessageResult sendInMessage(String messageBody){
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl(inUrl)
                .withMessageBody(messageBody)
                .withMessageGroupId("MPGroup")
                .withMessageDeduplicationId("DupId");
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


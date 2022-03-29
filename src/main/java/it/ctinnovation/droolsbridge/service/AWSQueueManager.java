package it.ctinnovation.droolsbridge.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class AWSQueueManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AWSQueueManager.class);

    @Autowired
    AWSCredentialsProvider inConsumer;

    @Autowired
    AWSCredentialsProvider inProducer;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${sqs.queues.in.url}")
    private String inUrl;

    private final String QUEUE_NAME = "drllsCTIQueue";
    private AmazonSQSClient inConsumerClient;
    private AmazonSQSClient inProducerClient;

    @PostConstruct
    private void initializeAmazonInConsumerSqsClient() {
        this.inConsumerClient =
                (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                        .withCredentials(inConsumer)
                        .withRegion(region)
                        .build();
    }

    @PostConstruct
    private void initializeAmazonInProducerSqsClient() {
        this.inProducerClient =
                (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                        .withCredentials(inProducer)
                        .withRegion(region)
                        .build();
    }

    public AmazonSQSClient getInConsumerClient() {
        return inConsumerClient;
    }
    public AmazonSQSClient getInProducerClient() {
        return inProducerClient;
    }

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

    public void deleteInMessage(Message message) {
        getInConsumerClient().deleteMessage(inUrl,message.getReceiptHandle());
    }

}


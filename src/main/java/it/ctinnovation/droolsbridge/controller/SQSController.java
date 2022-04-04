package it.ctinnovation.droolsbridge.controller;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import it.ctinnovation.droolsbridge.service.AWSQueueManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SQSController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQSController.class);

    @Autowired
    AWSQueueManager awsQueueManager;

    @PostMapping(value = "/sendMessage")
    public String sendMessage(@RequestBody String message){
        log.info("Messaggio:\n{}",message);
        awsQueueManager.sendInMessage(message);
        return "Inviato messaggio:\n"+message;
    }

    @GetMapping(value = "/receiveMessage")
    public String receiveMessage(){
        List<Message> messages=awsQueueManager.receiveInMessage();
        if(messages.isEmpty())
            return "Coda messaggi vuota";
        StringBuilder response_message=new StringBuilder();
        for(Message msg:messages){
            response_message.append(msg.getBody()+"\n");
        }
        return String.format("Ricevuti messaggi:\n%s ",response_message.toString());
    }
}

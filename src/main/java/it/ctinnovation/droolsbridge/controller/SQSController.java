package it.ctinnovation.droolsbridge.controller;

import com.amazonaws.services.sqs.model.Message;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
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
    SQSQueueManager awsQueueManager;

    @PostMapping(value = "/sendInMessage")
    public String sendInMessage(@RequestBody String message){
        log.info("Messaggio:\n{}",message);
        awsQueueManager.sendInMessage(message);
        return "Inviato messaggio:\n"+message;
    }

    @GetMapping(value = "/receiveInMessage")
    public String receiveInMessage(){
        List<Message> messages=awsQueueManager.receiveInMessage();
        if(messages.isEmpty())
            return "Coda messaggi vuota";
        StringBuilder response_message=new StringBuilder();
        for(Message msg:messages){
            response_message.append(msg.getBody()+"\n");
        }
        return String.format("Ricevuti messaggi:\n%s ",response_message.toString());
    }

    @PostMapping(value = "/sendOutMessage")
    public String sendOutMessage(@RequestBody String message){
        log.info("Messaggio OUT:\n{}",message);
        awsQueueManager.sendOutMessage(message);
        return "Inviato messaggio OUT:\n"+message;
    }

    @GetMapping(value = "/receiveOutMessage")
    public String receiveOutMessage(){
        List<Message> messages=awsQueueManager.receiveOutMessage();
        if(messages.isEmpty())
            return "Coda messaggi OUT vuota";
        StringBuilder response_message=new StringBuilder();
        for(Message msg:messages){
            response_message.append(msg.getBody()+"\n");
        }
        return String.format("Ricevuti messaggi:\n%s ",response_message.toString());
    }
}

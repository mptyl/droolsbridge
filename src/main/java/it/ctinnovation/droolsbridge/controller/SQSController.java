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

@RestController
public class SQSController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SQSController.class);

    @Autowired
    AWSQueueManager awsQueueManager;

    @PostMapping(value = "/sendMessage")
    public String sendMessage(@RequestBody String message){
        log.info("Messaggio: {}",message);
        awsQueueManager.sendInMessage(message);
        return "Inviato messaggio: "+message;
    }

    @GetMapping(value = "/receiveMessage")
    public String receiveMessage(){
        ReceiveMessageResult mr=awsQueueManager.receiveInMessage();
        if(mr.getMessages().size()==0)
            return "Coda messaggi vuota";
        StringBuilder response_message=new StringBuilder();
        for(Message msg:mr.getMessages()){
            response_message.append(msg.getBody()+"\n");
        }
        return String.format("Ricevuti messaggi %s: ",response_message.toString());
    }
}

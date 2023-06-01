package it.ctinnovation.droolsbridge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ctinnovation.droolsbridge.service.SetupService;
import it.ctinnovation.droolsbridge.service.aws.SQSQueueManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FakeController.class);

    @Autowired
    SQSQueueManager awsQueueManager;

    @Autowired
    SetupService setupService;

    @PostMapping(value = "/sendFakeInMessage")
    public String sendInMessage() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        awsQueueManager.sendInMessage(om.writeValueAsString(setupService.setCernusco()));
        awsQueueManager.sendInMessage(om.writeValueAsString(setupService.setCassina()));
        awsQueueManager.sendInMessage(om.writeValueAsString(setupService.setSegrate()));
        //awsQueueManager.sendInMessage(om.writeValueAsString(setupService.setPioltello()));

        return "creati Placemarks e PointAfAttention";
    }
}

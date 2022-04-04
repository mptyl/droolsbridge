package it.ctinnovation.droolsbridge;

import it.ctinnovation.droolsbridge.service.DroolsService;
import it.ctinnovation.droolsbridge.service.FactFeeder;
import it.ctinnovation.droolsbridge.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DroolsbridgeApplication implements CommandLineRunner, Thread.UncaughtExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Lazy
	private DroolsService droolService;

	@Autowired
	private MessageService messageService;

	@Autowired
	FactFeeder feeder;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DroolsbridgeApplication.class);
		//application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		// set the default uncaught Exception hendler
		Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) this);
		droolService.startAsyncSession();
		logger.info("Active Message Service is {} - {}", messageService.getName(), messageService.getDescr());
		messageService.start();
		feeder.start();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("FATAL uncaught Exception from Thread: " + t);
		logger.error("Exception is ", e);
	}

}

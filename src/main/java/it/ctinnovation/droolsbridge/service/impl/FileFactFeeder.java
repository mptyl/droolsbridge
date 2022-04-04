/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ctinnovation.droolsbridge.service.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.ctinnovation.droolsbridge.service.DroolsService;
import it.ctinnovation.droolsbridge.service.FactFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.joining;

/**
 * @author Luca Buraggi
 */
@ConfigurationProperties(prefix = "file-feeder")
public class FileFactFeeder implements FactFeeder {

	public static final String MODEL_PACKAGE = "it.ctinnovation.droolsbridge.model.";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<Path> factFiles;
	private volatile AtomicBoolean runFeeder = new AtomicBoolean(true);

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	@Lazy
	DroolsService droolsService;

	// values injected from config. file
	private Integer feederDelay;
	private Boolean showFacts;

	public FileFactFeeder(List<Path> factFiles) {
		this.factFiles = factFiles;
	}

	@Async
	@Override
	public void start() {
		logger.info("Starting Fact fedeer ...");
		logger.info("Data file(s): {}", factFiles);
		Map<Class, Integer> countMap = new HashMap<>();
		Integer total = 0;
		runFeeder.set(true);
		try {
			for (Path path : factFiles) {
				Integer count = 0;
				Class clazz = findClassOf(path);
				try {
					ObjectReader objReader = objectMapper.readerFor(clazz);
					MappingIterator it = objReader.readValues(path.toFile());
					while (it.hasNextValue()) {
						Object fact = it.nextValue();
						if (showFacts) {
							logger.info("New fact: {}", fact.toString());
						}
						droolsService.addToSession(fact);
						if (feederDelay > 0) {
							Thread.sleep(feederDelay);
						}
						count++;
					}
				} catch (IOException ioe) {
					logger.error("Unable to process {} - {}", path, ioe.getMessage());
				}
				countMap.put(clazz, countMap.getOrDefault(clazz, 0) + count);
				total += count;
			}
		} catch (InterruptedException ex) {
			logger.info(("Interrupted !"));
		} catch (IllegalArgumentException iae) {
			logger.error("Unknown Fact type -  {}", iae.getMessage());
		}
		logger.info("{} input json object processed.", total);
		logger.info(countMap.keySet().stream().map(k -> k.getSimpleName() + "=" + countMap.get(k)).collect(joining(", ", "{", "}")));
	}

	public void setFeederDelay(Integer feederDelay) {
		this.feederDelay = feederDelay;
	}

	private Class findClassOf(Path path) throws IllegalArgumentException {
		try {
			String className = MODEL_PACKAGE + path.getFileName().toString().split("\\.")[0].split("_")[0];
			logger.debug("The class name of {} is {}", path, className);
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Unable to desume the class of the Json stream " + path.getFileName());
		}
	}

	public void setShowFacts(Boolean showFacts) {
		this.showFacts = showFacts;
	}

	@Override
	public void stop() {
		runFeeder.set(false);
	}

}

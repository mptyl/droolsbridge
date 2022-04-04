/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ctinnovation.droolsbridge.config;

import it.ctinnovation.droolsbridge.service.impl.FileFactFeeder;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author Luca Buraggi - Horsa Devlab - Horsa Group
 */
@Configuration
@Profile("test")
@ConfigurationProperties(prefix = "file-feeder")
public class FileFactFeederConfig {

	private List<String> factFiles;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public FileFactFeeder fileFactFeeder() {
		List<Path> paths = new ArrayList<>();
		if (factFiles != null) {
			for (String fileName : factFiles) {
				paths.add(find(fileName));
			}
		} else {
			logger.warn("No json input files specified in configuration ! - doing nothing");
		}
		return new FileFactFeeder(paths);
	}

	public void setFactFiles(List<String> factFiles) {
		this.factFiles = factFiles;
	}

	private Path find(String fileName) throws NoSuchElementException {
		// try as it is (if it is absolute path it works)
		Path tryPath = Paths.get(fileName);
		if (Files.exists(tryPath)) {
			return tryPath;
		}
		// try from current directory
		tryPath = Paths.get("./test-data", fileName);
		if (Files.exists(tryPath)) {
			return tryPath;
		}
		// Path del jar / classes in esecuzione
		Path jarPath = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).toPath();
		tryPath = jarPath.resolve(tryPath);
		if (Files.exists(tryPath)) {
			return tryPath;
		}
		tryPath = jarPath.resolve(Paths.get("../test-data", fileName));
		if (Files.exists(tryPath)) {
			return tryPath;
		}
		tryPath = jarPath.resolve(Paths.get("../../test-data", fileName));
		if (Files.exists(tryPath)) {
			return tryPath.normalize();
		}
		throw new NoSuchElementException("Unable to find file:" + fileName);
	}

}

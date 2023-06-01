/*
 */

package it.ctinnovation.droolsbridge.service.drools;

import it.ctinnovation.droolsbridge.drools.DebugDroolsListener;
import it.ctinnovation.droolsbridge.drools.KieScannerListener;
import it.ctinnovation.droolsbridge.model.TheaterPointOfAttention;
import it.ctinnovation.droolsbridge.props.DroolsConfigProps;
import it.ctinnovation.droolsbridge.service.SetupService;
import it.ctinnovation.droolsbridge.util.ErrorUtil;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides the Drools services:
 * <ul>
 * <li>Engine initialization, Stateful session creation, KieScanner service startup</li>
 * <li>Session startup in a separate thread</li>
 * <li>Fact insertion in the session working memory service, Drools Query on demand run</li>
 * <li>Engine stop and clean-up</li>
 * </ul>
 * @author Luca Buraggi
 */
@Service
public class DroolsService {
	public static final String MESSAGE_SERVICE = "MessageService";
	public static final String RULE_LOGGER = "RuleLogger";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private KieRuntimeLogger kieRuntimeLogger;
	/**
	 * Logger available as global in the rule file for generating log messages from rules
	 */
	private final Logger ruleLogger = LoggerFactory.getLogger(RULE_LOGGER);

	// configuration properties coming from the application.yml file (only properties with "drools" are here
	@Autowired
	private DroolsConfigProps droolsConfigProps;

	@Autowired

	SetupService setupService;

	@Autowired
	private KieContainer kieContainer;

	// Drools component scanning at fixed intervals the configured Maven repository for a new rule package version
	@Autowired
	private KieScanner kieScanner;

	// Logs kieScanner actions, signalling when a new ruleset is loaded in the current Session
	@Autowired
	private KieScannerListener kieScannerListener;

	@Autowired
	private MessageService messageService;

	private KieBase kieBase;
	private KieSession kieSession;
	// start / stop the alternative optional KieScanner thread
	private volatile AtomicBoolean executeKieScan = new AtomicBoolean(true);

	@PostConstruct
	private void init() {
		logger.info("Initializing now Drools Service");
		logger.info("Configured active DROOLS Knowledge Base is {}", droolsConfigProps.getKieBaseName());
		logger.info("Configured active DROOLS Knowledge Session is {}", droolsConfigProps.getKieSessionName());
		try {
			logger.info("The active Rule set is: {}", kieContainer.getReleaseId().toString());
			kieBase = kieContainer.getKieBase(droolsConfigProps.getKieBaseName());
			kieSession = kieContainer.newKieSession(droolsConfigProps.getKieSessionName());
			logger.info("The new Drools Session creation is succesful.");
			try {
				kieSession.setGlobal(RULE_LOGGER, ruleLogger);
				kieSession.setGlobal(MESSAGE_SERVICE, messageService);
			} catch (Exception e) {
				// if the RULE_LOGGER global is not defined in .drl skip
				logger.warn("Initialization of global " + RULE_LOGGER + " failed. (It is declared in .drl ?): " + e);
			}
			if (droolsConfigProps.getLogEnabled()) {
				kieRuntimeLogger = KieServices.get().getLoggers().newFileLogger(kieSession, droolsConfigProps.getLogFile());
			}
			if (droolsConfigProps.getDebugEnabled()) {
				kieSession.addEventListener(new DebugDroolsListener(ruleLogger));
			}
			kieScanner.addListener(kieScannerListener);
			logger.info("KieScanner custom:{} scanning interval {} mSec", droolsConfigProps.isCustomKieScanner(), droolsConfigProps.getKieScannerInterval());
			executeKieScan.set(droolsConfigProps.isCustomKieScanner());
			if (!droolsConfigProps.isCustomKieScanner()) {
				// run the KieScanner internal scheduler
				kieScanner.start(droolsConfigProps.getKieScannerInterval());
			} else {
				// run a scheduled Thread calling kieScanner.scanNow()
				startCustomKieScanner();
			}

			TheaterPointOfAttention poaPioltello=setupService.setPioltello();
			TheaterPointOfAttention poaVimercate=setupService.setVimercate();
			kieSession.insert(poaPioltello);
			logger.info("Loading POA: {}", poaPioltello.toString());
			kieSession.insert(poaVimercate);
			logger.info("Loading POA: {}", poaVimercate.toString());

		} catch (Exception e) {
			throw new RuntimeException("DROOLS initialization error.", e);
		}
	}

	/**
	 * This is an optional implementation of the KieScanner background activity. Sometimes, under heavy load conditions,
	 * the builtin internal KieScanner thread seems to vanish. If this happens use this alternative setting
	 * custom-kie-scanner = true in configuration.
	 */
	public void startCustomKieScanner() {
		new Thread(() -> {
			logger.info("{}  starts", Thread.currentThread().getName());
			while (executeKieScan.get()) {
				try {
					kieScanner.scanNow();
					Thread.sleep(droolsConfigProps.getKieScannerInterval());
				} catch (Exception e) {
					logger.error("Error while scanning rules " + e, e);
				}
			}
			logger.info("{} shutdown", Thread.currentThread().getName());
		}, "KieScanner executor").start();
	}

	/**
	 * Insert a Fact in the current Session working memory
	 * @param <T>
	 * @param data the Fact
	 */
	public <T> void addToSession(T data) {
		getKieSession().insert(data);
	}

	/**
	 * Insert a Fact collection in the current Session working memory
	 * @param dataList
	 */
	public void addToSession(Collection<?> dataList) {
		getKieSession().submit(kSession -> dataList.forEach(o -> kSession.insert(o)));
	}

	/**
	 * Start the stateful Session in a separate thread.
	 * The DROOLS engine runs in background waiting for new Facts and evaluating the Rules until stopped
	 */
	@Async
	public void startAsyncSession() {
		try {
			logger.info("Starting DROOLS engine now.");
			getKieSession().fireUntilHalt();
		} catch (RuntimeException e) {
			logger.error("Drools engine runtime error: " + ErrorUtil.getCauses(e));
			throw e;
		}
	}

	/**
	 * Called by Springboot during the shutdown. Stops the engine and cleans up resources
	 */
	@PreDestroy
	public void dispose() {
		logger.info("Shutting down DROOLS engine now.");
		if (kieScanner != null) {
			kieScanner.shutdown();
		}
		if (kieRuntimeLogger != null) {
			kieRuntimeLogger.close();
		}
		if (getKieSession() != null) {
			getKieSession().halt();
		}
		if (kieContainer != null) {
			kieContainer.dispose();
		}

		executeKieScan.set(false);
	}

	/**
	 * If the Ruleset defines one or more Drools queries, call this method to run on demand the specified query
	 * @param <T>
	 * @param queryName
	 * @return
	 */
	public <T> T runDroolsQuery(String queryName) {
		if (kieSession != null) {
			try {
				QueryResults results = kieSession.getQueryResults(queryName);
				if (results.size() >= 1) {
					T res = (T) results.iterator().next().get("$result");
					logger.info("{} working memory query result: {}", queryName, res);
					return res;
				}
			} catch (RuntimeException e) {
				// it is ok
			}
		}
		return null;
	}

	public KieScanner getKieScanner() {
		return kieScanner;
	}

	public KieBase getKieBase() {
		return kieBase;
	}

	public KieSession getKieSession() {
		return kieSession;
	}

}

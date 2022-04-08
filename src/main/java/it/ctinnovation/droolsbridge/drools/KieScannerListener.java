/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ctinnovation.droolsbridge.drools;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.ctinnovation.droolsbridge.service.drools.DroolsService;
import it.ctinnovation.droolsbridge.service.drools.MessageService;
import it.ctinnovation.droolsbridge.util.ErrorUtil;
import org.kie.api.builder.Message;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Luca Buraggi - Horsa Devlab - Horsa Group
 */
@Component
public class KieScannerListener implements KieScannerEventListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Lazy
	private MessageService messageService;

	@Autowired
	@Lazy
	private DroolsService droolsService;

	@Override
	public void onKieScannerStatusChangeEvent(KieScannerStatusChangeEvent statusChange) {
		String msg;
		switch (statusChange.getStatus()) {
			case RUNNING:
			case STOPPED:
				return;
			case SCANNING:
				msg = "Scanning remote Rules repository for any updated Rule-set";
				break;
			default:
				msg = "Drools repository scanner status is " + statusChange.getStatus();
		}
		logger.info(msg);
	}

	@Override
	public void onKieScannerUpdateResultsEvent(KieScannerUpdateResultsEvent updateResults) {
		try {
			logger.info("Rules update process terminated.");
			List<Message> messages = updateResults.getResults().getMessages();
			if (!messages.isEmpty()) {
				logger.warn("KModule update: " + showMessages(messages));
			}
			sendRules();
		} catch (Exception e) {
			logger.error("Error sending the new Drools ruleset after a rule upgrade event. " + ErrorUtil.getCauses(e));
		}
	}

	private void sendRules() {
		List<Rule> rules = droolsService.getKieBase().getKiePackages().stream().map(p -> p.getRules()).flatMap(Collection::stream).collect(toList());
		RuleDescriptorList ruleDescriptorList = new RuleDescriptorList();
		ruleDescriptorList.setExportedRules(rules.stream().map(r -> new RuleDescriptor(r)).collect(toList()));
		messageService.sendRules(ruleDescriptorList);
	}

	private String showMessages(List<Message> messages) {
		final StringBuilder sb = new StringBuilder("---\n");
		messages.forEach(m -> sb.append(m.getText()).append("\n"));
		sb.append("---\n");
		return sb.toString();
	}

	public static class RuleDescriptorList {

		@JsonProperty(value = "drools_rules")
		List<RuleDescriptor> exportedRules;

		@Override
		public String toString() {
			return "exportedRules=" + exportedRules;
		}

		public List<RuleDescriptor> getExportedRules() {
			return exportedRules;
		}

		public void setExportedRules(List<RuleDescriptor> exportedRules) {
			this.exportedRules = exportedRules;
		}
	}

	public static class RuleDescriptor {

		private final String name;
		private final String namespace;

		public RuleDescriptor(Rule rule) {
			this.name = rule.getName();
			this.namespace = rule.getPackageName();
		}

		@Override
		public String toString() {
			return namespace + "." + name;
		}

		public String getName() {
			return name;
		}

		public String getNamespace() {
			return namespace;
		}
	}
}

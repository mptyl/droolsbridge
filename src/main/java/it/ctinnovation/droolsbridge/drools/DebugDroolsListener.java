/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ctinnovation.droolsbridge.drools;

import java.util.HashMap;
import java.util.Map;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.slf4j.Logger;

/**
 *
 * @author Luca Buraggi - Horsa Devlab - Horsa Group
 */
public class DebugDroolsListener extends DefaultAgendaEventListener {

	private final Logger ruleLogger;
	private final Map<String, Long> ruleActivationMap;

	public DebugDroolsListener(Logger ruleLogger) {
		this.ruleLogger = ruleLogger;
		ruleActivationMap = new HashMap<>();
	}

	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		Rule rule = event.getMatch().getRule();
		Long currentValue = ruleActivationMap.putIfAbsent(rule.getName(), 1L);
		if (currentValue != null) {
			if (currentValue == Long.MAX_VALUE) {
				currentValue = 0L;
			}
			ruleActivationMap.put(rule.getName(), ++currentValue);
		}
	}
}

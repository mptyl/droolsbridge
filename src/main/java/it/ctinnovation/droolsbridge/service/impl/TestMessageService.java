/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.ctinnovation.droolsbridge.service.impl;

import it.ctinnovation.droolsbridge.drools.KieScannerListener;
import it.ctinnovation.droolsbridge.service.MessageService;
import org.springframework.stereotype.Service;

/**
 *
 * @author Luca Buraggi
 */
@Service
public class TestMessageService implements MessageService {

	@Override
	public void sendRules(KieScannerListener.RuleDescriptorList ruleDescriptorList) {
	}

	@Override
	public void start() {
	}

	@Override
	public String getName() {
		return "Test Message Service";
	}

	@Override
	public String getDescr() {
		return "Generates a stream of test facts";
	}

}

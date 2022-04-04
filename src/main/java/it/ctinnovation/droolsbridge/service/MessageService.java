/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package it.ctinnovation.droolsbridge.service;

import it.ctinnovation.droolsbridge.drools.KieScannerListener;

/**
 *
 * @author Luca Buraggi
 */
public interface MessageService {

	public void sendRules(KieScannerListener.RuleDescriptorList ruleDescriptorList);

	public void start();
	
	public default String getName(){
		return "Default Message Service";
	}

	public default String getDescr(){
		return "Description of Default Message Service";
	}

}

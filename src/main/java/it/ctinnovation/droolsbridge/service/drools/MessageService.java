/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package it.ctinnovation.droolsbridge.service.drools;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.ctinnovation.droolsbridge.drools.KieScannerListener;
import it.ctinnovation.droolsbridge.model.Asset;

/**
 *
 * @author Luca Buraggi
 */
public interface MessageService {

	public default void sendRules(KieScannerListener.RuleDescriptorList ruleDescriptorList){};

	public void start();

	public void stop();
	
	public default String getName(){
		return "Default Message Service";
	}

	public default String getDescr(){
		return "Description of Default Message Service";
	}

	public void sendMessage(Asset asset) throws JsonProcessingException;

}

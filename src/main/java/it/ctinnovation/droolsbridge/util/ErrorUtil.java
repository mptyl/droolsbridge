/*
 *  ErrorUtil.java
 *  Author: Luca Buraggi - Italy
 *
 *  Created on 30-ott-2009
 *
 *  $Source: /home/matecvs/MateUtil/src/it/mate/exception/ErrorUtil.java,v $
 *  $Revision: 1.1 $
 *  $Date: 2009-11-24 13:40:30 $
 *  $Author: luca $
 *  $Name:  $
 */

package it.ctinnovation.droolsbridge.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Buraggi - Italy
 */
public class ErrorUtil {

	public static String getCauses(Throwable e) {
		String msg = e.getClass().getName() + ": " + e.getMessage();
		if (e.getCause() == null || e.getCause().equals(e)) {
			return msg;
		}
		return msg + "\nCaused by " + getCauses(e.getCause());
	}

	public static List<String> getMessages(Throwable e) {
		List<String> l = new ArrayList<>();
		l.add(e.getLocalizedMessage());
		if (e.getCause() == null || e.getCause().equals(e)) {
			return l;
		}
		l.addAll(getMessages(e.getCause()));
		return l;
	}
}

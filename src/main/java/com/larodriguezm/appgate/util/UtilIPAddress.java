package com.larodriguezm.appgate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilIPAddress {

	private static final String PATTERN = "^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$";

	public static boolean validate(String ip) {
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	public static Integer ipAddressDecimal(String ip) {
		String[] octetos = ip.split("\\.");
		return 	(Integer.parseInt(octetos[0]) * 16777216) + 
				(Integer.parseInt(octetos[1]) * 65536) + 
				(Integer.parseInt(octetos[2]) * 256) + 
				Integer.parseInt(octetos[3]);
	}
}

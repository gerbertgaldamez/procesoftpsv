package com.terium.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomDate {
	@SuppressWarnings("unchecked")
	private static List<String> casesToTry() {
		@SuppressWarnings("rawtypes")
		List listC = new ArrayList();
		listC.add("yyyyMMdd HHmmss");
		listC.add("dd/MM/yyyy HH:mm:ss");
		listC.add("dd/MM/yyyy");
		listC.add("yyyyMMdd");
		listC.add("yyyyMMddHHmmss");
		listC.add("yyyy-MM-dd HH:mm:ss");
		listC.add("yyyy-MM-dd");
		listC.add("yyMMdd");
		return listC;
	}

	public String getFormatDate(String dateWithoutFormat, String confFormat) {
		SimpleDateFormat sdfG = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return tryCasesV2(dateWithoutFormat, sdfG, confFormat);
	}
	
	public String getFormatFecha(String dateWithoutFormat, String confFormat) {
		SimpleDateFormat sdfG = new SimpleDateFormat("dd/MM/yyyy");
		return tryCasesV2(dateWithoutFormat, sdfG, confFormat);
	}

	private String tryCasesV2(String dateWithoutFormat, SimpleDateFormat sdfG,
			String confFormat) {
		String strResp = "";
		Date convertedDate = null;
		convertedDate = tryFormat(dateWithoutFormat, confFormat);
		if (convertedDate != null) {
			strResp = sdfG.format(convertedDate);
			if ("".equals(strResp)) {
				tryCases(dateWithoutFormat, sdfG);
			}
		}
		return strResp;
	}
	
	private String pruebaCasesV2(String dateWithoutFormat,
			SimpleDateFormat sdfG, String confFormat) {
		String strResp = "";
		Date convertedDate = null;
		convertedDate = pruebaFormat(dateWithoutFormat, confFormat);
		if (convertedDate != null) {
			strResp = sdfG.format(convertedDate);
			if ("".equals(strResp)) {
				pruebaCases(dateWithoutFormat, sdfG);
			}
		}
		return strResp;
	}

	private Date tryFormat(String dateWithoutFormat, String formatUse) {
		DateFormat formatter = null;
		Date convertedDate = null;
		try {
			formatter = new SimpleDateFormat(formatUse);
			convertedDate = (Date) formatter.parse(dateWithoutFormat.trim());
		} catch (ParseException ex) {
			Logger.getLogger(CustomDate.class.getName()).log(Level.INFO, null,
					ex.getMessage());
			return null;
		}
		return convertedDate;
	}
	
	private Date pruebaFormat(String dateWithoutFormat, String formatUse) {
		DateFormat formatter = null;
		Date convertedDate = null;
		try {
			formatter = new SimpleDateFormat(formatUse);
			convertedDate = (Date) formatter.parse(dateWithoutFormat.trim());
		} catch (ParseException ex) {
			Logger.getLogger(CustomDate.class.getName()).log(Level.INFO, null,
					ex.getMessage());
			return null;
		}
		return convertedDate;
	}

	private String tryCases(String dateWithoutFormat, SimpleDateFormat sdfG) {
		String strResp = "";
		Date convertedDate = null;
		Iterator<String> itCases = casesToTry().iterator();
		while (itCases.hasNext()) {
			convertedDate = tryFormat(dateWithoutFormat, itCases.next());
			if (convertedDate != null) {
				strResp = sdfG.format(convertedDate);
				break;
			}
		}
		return strResp;
	}
	
	private String pruebaCases(String dateWithoutFormat, SimpleDateFormat sdfG) {
		String strResp = "";
		Date convertedDate = null;
		Iterator<String> itCases = casesToTry().iterator();
		while (itCases.hasNext()) {
			convertedDate = pruebaFormat(dateWithoutFormat, itCases.next());
			if (convertedDate != null) {
				strResp = sdfG.format(convertedDate);
				break;
			}
		}
		return strResp;
	}
}

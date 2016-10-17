package com.directv.edm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {
	
	public static boolean isContains(Set<String> target, StringBuilder strBuilder) {
		for (String str : target) {
			if (strBuilder.indexOf(str) > -1){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNotEmpty(StringBuilder builder) {
		if (builder != null && builder.length() != 0)
			return true;
		
		return false;
	}
	
	public static boolean isNotEmpty(Set<StringBuilder> builderSet) {
		if (builderSet != null && builderSet.size() != 0)
			return true;
		
		return false;
	}

	public static InputSource getInputsource(String dirFile) {
		InputSource is = null;
		try {
			InputStream inputStream = new FileInputStream(new File(dirFile));
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.format("Unsupported Encoding Exception ", e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.format("File Not Found Exception", e.getMessage());
		}
		return is;
	}

	public static SAXParser getSAXParser() {
		try {
			return SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException e) {
			System.out.format("Parser Configuration Exception: %s", e.getMessage());
		} catch (SAXException e) {
			System.out.format("SAX Exception : %s", e.getMessage());
		}
		return null;
	}
	
	public static String byteToString(byte b) {
		String text = null;

		if (b < 0)
			text = "";
		else if (b < 10)
			text = String.valueOf("0" + b);
		else
			text = String.valueOf(b);
		return text;
	}
	
	public static byte stringToByte(String b) {
		byte value;
		
		try {
			value = Byte.valueOf(b).byteValue();
		} catch (Exception e){
			System.out.format("Exception %s\n", e.getMessage());
			value = -1;
		}
		return value;
	}
	
	public static String getElement(String[] arrDir, String text) {
		String str = null;
		if (arrDir == null || arrDir.length == 0 || text == null ||text.isEmpty() )
			return str;
		
		int length = arrDir.length;
		for (int i = 0; i < length; i++) {
			if (arrDir[i].startsWith(text)) {
				str = arrDir[i];
				break;
			}
		}
		
		return str;
	}
	
	public static String getCollectionAtIndext(Set<String> set, int index) {
		if (index < 0 || set == null || set.size() == 0 || index >= set.size())
			return null;
		
		int i = 0;
		String tmsId = "";
		Iterator<String> it = set.iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			tmsId = it.next();
			if (i++ == index) {
				found = true;
			}
		}
		
		if (found)
			return tmsId;
		else
			return null;
	}
	
	public static int randInt(int min, int max) {
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static String getDateFormat(){
		Calendar calendar = Calendar.getInstance();
		String format = new SimpleDateFormat("-yyyyMMdd_HHmmss_").format(calendar.getTime());
		return format;
	}
	
	public static void main(String[] args){
		System.out.println(getDateFormat());
		
		
	}
}

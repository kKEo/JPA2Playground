package org.maziarz.jpa2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class JavaSourceInputScanner {

public static Map<String, String> process(String content) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		
		String className = null;
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			while ((line = br.readLine()) != null) {

				if (line.trim().startsWith("---")) {
					if (className != null) {
						res.put(className, sb.toString());
						sb.setLength(0);
					}
					className = line.substring(line.indexOf("---") + 3, line.length()).trim();	
					sb.append("import javax.persistence.*;").append(System.getProperty("line.separator"));
					sb.append("import java.util.*;").append(System.getProperty("line.separator"));
				} else 
				if (className != null) {
					sb.append(line).append(System.getProperty("line.separator"));
				}
				
			}
			res.put(className, sb.toString());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return res;
		
	}
	
}

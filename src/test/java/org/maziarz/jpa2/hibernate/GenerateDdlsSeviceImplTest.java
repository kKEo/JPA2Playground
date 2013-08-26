package org.maziarz.jpa2.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.util.io.IOUtils;
import org.junit.Test;
import org.maziarz.jpa2.GenerateDdlsSeviceImpl;

public class GenerateDdlsSeviceImplTest {

	@Test
	public void testService() throws Exception {

		GenerateDdlsSeviceImpl s = new GenerateDdlsSeviceImpl();

		Map<String, String> toBeProcessed = new HashMap<String, String>();

		addJavaSource(toBeProcessed, "Ticket");
		addJavaSource(toBeProcessed, "Priority");
		addJavaSource(toBeProcessed, "Requirement");

		System.out.println(s.generate(toBeProcessed));

	}

	@Test
	public void testService2() throws Exception {

		GenerateDdlsSeviceImpl s = new GenerateDdlsSeviceImpl();

		Map<String, String> toBeProcessed = new HashMap<String, String>();

		addJavaSource(toBeProcessed, "SimpleTicket");

		System.out.println(s.generate(toBeProcessed));

	}

	private void addJavaSource(Map<String, String> map, String className) throws IOException {
		InputStream is = GenerateDdlsSeviceImplTest.class.getClassLoader().getResourceAsStream(className + ".txt");
		map.put(className, IOUtils.toString(is));
	}

}

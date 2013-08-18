package org.maziarz.jpa2.hibernate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class GenerateDDLs {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		if (args.length < 2) {
			System.out.println("At least one argument required.");
			System.exit(1);
		}
		
		System.out.println("Args: "+Arrays.asList(args));
		
		File outputFile = new File(args[0]); 
		
		Class<?>[] classes = new Class<?>[args.length-1];
		
		for (int i = 1; i < args.length; i++) {
			classes[i-1] = Class.forName(args[i]);
		}
		
		List<String> stmts = new GenerateDDLs().printDdl(classes);
		
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		for (String stmt : stmts) {
			IOUtils.write(stmt, fos);
			IOUtils.write("\n---\n", fos);
		}
		
	}
	
	public List<String> printDdl(Class<?> ... classes) {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		for (Class<?> c : classes ) {
			configuration.addAnnotatedClass(c);
		}

		final Dialect dialect = Dialect.getDialect(configuration.getProperties());
		
		String[] stmts = configuration.generateSchemaCreationScript(dialect);

		List<String> formatedStmts = new ArrayList<String>(); 
		
		for (String s : stmts) {
			formatedStmts.add(FormatStyle.DDL.getFormatter().format(s));
		}
		
		return formatedStmts;
	}
	
	
}

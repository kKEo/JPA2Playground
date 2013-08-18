package org.maziarz.jpa2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateDdlsSeviceImpl implements GenerateDdlsService{

	private File tmpDir;

	private static Logger logger;

	static {
		logger = LoggerFactory.getLogger(GenerateDdlsSeviceImpl.class);
	}

	@Override
	public String generate(Map<String, String> sources) {
		try {
			return generateInternal(sources);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String generateInternal(Map<String, String> sources) throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException,
			InstantiationException {

		File[] files = storeSourcesInWorkingDirectory(sources);
		File jarFile = compileAndBuildJar(files);
		String sqlScript = generateDdls(jarFile, new ArrayList<String>(sources.keySet()));

		return sqlScript;

	}

	private File[] storeSourcesInWorkingDirectory(Map<String, String> sources) throws FileNotFoundException, IOException {
		long timeInMillis = 0;// GregorianCalendar.getInstance().getTimeInMillis();

		tmpDir = new File("/tmp", "jpa-tmp-" + timeInMillis);
		tmpDir.mkdir();

		logger.debug("" + tmpDir);

		File[] files = new File[sources.size()];
		int i = 0;
		for (Entry<String, String> e : sources.entrySet()) {

			File f = new File(tmpDir.toString(), e.getKey() + ".java");
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			IOUtils.write(e.getValue(), fileOutputStream);
			fileOutputStream.close();

			files[i++] = f;

		}
		return files;
	}

	private String generateDdls(File jarFile, List<String> toGenerate) throws FileNotFoundException, IOException {

		String classPath = System.getProperty("java.class.path");
		String fileSeparator = System.getProperty("path.separator");
		classPath = jarFile.getAbsolutePath() + fileSeparator + classPath;

		logger.debug(classPath);

		File out = new File(jarFile.getAbsolutePath().replaceFirst("jar$", "sql"));

		List<String> command = Arrays.asList("java", "-cp", classPath, "org.maziarz.jpa2.hibernate.GenerateDDLs",
				out.getAbsolutePath());
		command = new ArrayList<String>(command);
		command.addAll(toGenerate);

		System.out.println(command);

		ProcessBuilder pb = new ProcessBuilder(command);

		try {
			Process p = pb.start();

			InputStream is = p.getInputStream();
			InputStream err = p.getErrorStream();

			p.waitFor();

			System.out.write(IOUtils.toByteArray(is));
			System.out.write(IOUtils.toByteArray(err));

			is.close();
			err.close();

		} catch (Exception e) {
			logger.error("Cannot generate DDLs", e);
		}

		FileInputStream result = new FileInputStream(out);
		try {
			return IOUtils.toString(result);
		} finally {
			result.close();
		}

	}

	private File compileAndBuildJar(File[] files) throws IOException, MalformedURLException, FileNotFoundException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
		boolean success = task.call();
		fileManager.close();
		logger.info("Compilation success?? - " + success);

		if (diagnostics.getDiagnostics().size() > 0) {
			logger.error("" + diagnostics.getDiagnostics());
			throw new RuntimeException("Compilation failed: " + diagnostics.getDiagnostics());
		}

		for (JavaFileObject cu : compilationUnits) {
			logger.debug("" + cu.toUri().toURL());
		}

		byte[] buffer = new byte[1024];

		File jarFile = new File(tmpDir, "/model.jar");
		FileOutputStream fos = new FileOutputStream(jarFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		zos.putNextEntry(new ZipEntry("META-INF/"));

		ZipEntry manifest = new ZipEntry("META-INF/MANIFEST.MF");
		zos.putNextEntry(manifest);

		StringBuilder sb = new StringBuilder();
		sb.append("Manifest-Version: 1.0").append("\n");
		sb.append("Main-Class: Ticket").append("\n");
		String content = sb.toString();

		zos.write(content.getBytes());
		zos.closeEntry();

		for (File f : files[0].getParentFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		})) {

			File classFile = new File(f.getAbsolutePath());// .replaceFirst("\\.java$", "\\.class"));

			logger.debug("Zipping: " + classFile);
			ZipEntry ze = new ZipEntry(classFile.getName());
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(classFile);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();
		}
		zos.close();

		return jarFile;
	}



}

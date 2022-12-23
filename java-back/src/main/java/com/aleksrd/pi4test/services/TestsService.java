
package com.aleksrd.pi4test.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.test.JMeterTestRequestDto;
import com.aleksrd.pi4test.dto.test.UnitTestRequestDto;
import com.aleksrd.pi4test.entities.Project;
import com.aleksrd.pi4test.entities.Testing;
import com.aleksrd.pi4test.enums.TestType;
import com.aleksrd.pi4test.repositories.TestsRepository;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@Service
@Transactional
@CommonsLog
public class TestsService {
	
	//Unit Test 
	private final String RESOURCES_FOLDER = "/src/main/resources/";
	
	private final String COMPILE_PROJECT_SCRIPT = "compileProject.sh";
	private final String CREATE_DATABASE_SCRIPT = "createDatabase.sh";
	private final String EXECUTE_UNIT_SCRIPT = "executeUnitTest.sh";
	private final String SUREFIRE_SCRIPT = "surefire.sh";
	private final String JS_ADDON_SCRIPT = "addon.js";
	private final String JS_ADDON_LOCATION = "/target/site/addon.js";

	private final String PI4TEST_MOCK_USER = "pi4testMockUser";
	private final String PI4TEST_MOCK_PASS = "abcd@L8D(G)";
	
	//Performance Tests (JMeter)
	private final String JMETER_HOME = Utils.getHomeDir() + "/apache-jmeter-5.5";
	private final String JMETER_PROPERTIES = Utils.getHomeDir() + "/apache-jmeter-5.5/bin/jmeter.properties";
	private final String JMETER_RESULT_FILE = "/JMeterResults.csv";
	private final String JMETER_UPLOAD_TESTS_FOLDER = "/JMeterTests";

	@Autowired
	private TestsRepository testRepository;
	@Autowired
	private ProjectService projectService;

	public List<Testing> findAllByProject(Integer projectId) {
		return testRepository.findAllByProjectId(projectId);
	}
	
	public void deleteTest(Integer projectId) {
		testRepository.deleteByProjectId(projectId);
	}

	public Testing saveTest(Project project, TestType type, String elapsedTime) {
		Testing test = new Testing();
		test.setProject(project);
		test.setUidTest(UUID.randomUUID().toString());
		test.setType(type);
		test.setExecutedAt(LocalDateTime.now());
		test.setElapsedTime(elapsedTime);
		Testing saved = testRepository.save(test);
		testRepository.flush();
		return saved;
	}
	
	public Integer getRandomPort() {
		log.debug("getRandomPort() - Getting random port...");
		SecureRandom sr = new SecureRandom();
		int minPort = 49152;
		int maxPort = 65535;
		Integer port = sr.nextInt((maxPort - minPort + 1) + minPort);
		log.debug("getRandomPort() - Port: " + port);
		return port;
	}
	
	public void prepareUnitTest(UnitTestRequestDto dto) {
		try {
			Project project = projectService.getProjectIfLoggedIsOwner(dto.projectUid());	
			File sqlLocation = new File(project.getSystemPath() + "/db.sql");
			dto.sqlFile().transferTo(sqlLocation);
			executeUnitTest(project, dto.dbName(), sqlLocation.getPath());
		} catch (Exception e) {
			log.error("doUnitTest() - An error ocurred while processing the tests", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	private void executeUnitTest(Project project, String dbName, String sqlLocation) throws IOException {
			log.debug("doUnitTest() - Deleting 'skipTests' tag");
			Utils.executeCommand(new String[] { "/bin/sed", "-i", "/skipTests/d", "pom.xml" }, new File(project.getSystemPath()));

			log.debug("doUnitTest() - Changing datasource...");
			dbName = dbName + project.getId();
			String sedDataSource = "/localhost/cspring.datasource.url = jdbc:mysql://localhost:3306/" + dbName
			        + "?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true";
			Utils.executeCommand(new String[] { "/bin/sed", "-i", sedDataSource, "application.properties" }, new File(project.getSystemPath() + RESOURCES_FOLDER));

			log.debug("doUnitTest() - Changing username...");
			String sedUsername = "/spring.datasource.username/cspring.datasource.username=" + PI4TEST_MOCK_USER;
			Utils.executeCommand(new String[] { "/bin/sed", "-i", sedUsername, "application.properties" }, new File(project.getSystemPath() + RESOURCES_FOLDER));

			log.debug("doUnitTest() - Changing password...");
			String sedPass = "/spring.datasource.password/cspring.datasource.password=" + PI4TEST_MOCK_PASS;
			Utils.executeCommand(new String[] { "/bin/sed", "-i", sedPass, "application.properties" }, new File(project.getSystemPath() + RESOURCES_FOLDER));

			log.debug("doUnitTest() - Creating database: " + dbName);
			String createDBScriptLocation = Utils.chargeScript(CREATE_DATABASE_SCRIPT);
			Utils.executeCommand(new String[] { "/bin/bash", createDBScriptLocation, dbName, sqlLocation }, new File(Utils.getHomeDir()));

			log.debug("doUnitTest() - Executing unit tests");
			Long start = System.currentTimeMillis();
			String execTestScriptLocation = Utils.chargeScript(EXECUTE_UNIT_SCRIPT);
			Utils.executeCommand(new String[] { "/bin/bash", execTestScriptLocation }, new File(project.getSystemPath()));
			Long finish = System.currentTimeMillis();
			Long elapsedTime = (finish - start)/1000;
			log.info("executeUnitTest() - Took " + elapsedTime + " seconds to execute the unit tests");
			
			log.debug("doUnitTest() - Generating report...");
			Utils.executeCommand(new String[] { "mvn", "surefire-report:report-only" }, new File(project.getSystemPath()));
			
			log.debug("doUnitTest() - Adding bootstrap libraries to the report");
			String surefireScriptLocation = Utils.chargeScript(SUREFIRE_SCRIPT);
			Utils.executeCommand(new String[] { "/bin/bash", surefireScriptLocation, project.getSystemPath() }, new File(Utils.getHomeDir()));

			String addonLocation = Utils.chargeScript(JS_ADDON_SCRIPT);
			Files.copy(Path.of(addonLocation), Path.of(project.getSystemPath() + JS_ADDON_LOCATION), StandardCopyOption.REPLACE_EXISTING);
			
			String totalTestTime = DateFormatUtils.format(elapsedTime*1000, "m'm':ss's'");
			
			saveTest(project, TestType.FUNCTIONAL, totalTestTime);
			project.setDatabaseName(dbName);
			project.setUnitTestExecuted(true);
			projectService.update(project);
	}
	
	
	public void doPerformanceTests(JMeterTestRequestDto dto) {
		Project project = projectService.getProjectIfLoggedIsOwner(dto.projectUid());

		try {
			MultipartFile zipFile = dto.zipFile();
			String pathToTransfer = project.getSystemPath() + "/" + zipFile.getOriginalFilename();
			zipFile.transferTo(new File(pathToTransfer));
			File[] files = decompressPerfomanceTests(project, pathToTransfer);

			if (files.length != 0) {
				String jarName = compileProjectToPerformanceTest(project);
				
				log.debug("doPerformanceTests() - Starting the test in port: " + dto.deployPort());
				Process exec = Runtime.getRuntime().exec(new String[] { "java", "-jar", "-Dserver.port=" + dto.deployPort(), jarName, "&" }, null, new File(project.getSystemPath() + "/target/"));
				String csvLocation = project.getSystemPath() + JMETER_RESULT_FILE;
				List<File> tests = Arrays.asList(files);
				String elapsedTime = executeJMeterTests(tests, csvLocation);
				exec.destroy();
				

				saveTest(project, TestType.PERFORMANCE, elapsedTime);
				project.setPerformanceTestExecuted(true);
				projectService.update(project);
			}
		} catch (Exception e) {
			if (e instanceof ResponseStatusException) {
				throw (ResponseStatusException) e;
			} else {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
			}
		}
	}
	
	private File[] decompressPerfomanceTests(Project project, String pathToTransfer) {
			log.debug("doPerformanceTests() - Decompressing the ZIP file...");
			Utils.executeCommand(new String[] { "/bin/bash", "7z", "x", pathToTransfer, "-aoa", "-o" + "JMeterTests" }, new File(project.getSystemPath()));
			Utils.executeCommand(new String[] { "/bin/rm", "-rf", pathToTransfer }, new File(project.getSystemPath()));
			File projectPath = new File(project.getSystemPath() + JMETER_UPLOAD_TESTS_FOLDER);
			File[] files = projectPath.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {
					return name.endsWith(".jmx");
				}
			});
			return files;
		}

	private String compileProjectToPerformanceTest(Project project) {
			log.debug("preparareJMeterTests() - Compiling project: " + project.getTitle());
			String compileScriptLocation = Utils.chargeScript(COMPILE_PROJECT_SCRIPT);
			Utils.executeCommand(new String[] { "/bin/bash", compileScriptLocation, project.getSystemPath() }, new File(project.getSystemPath()));

			File targetFolder = new File(project.getSystemPath() + "/target");
			File[] files = targetFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {
					return name.endsWith(".jar");
				}
			});

			if (files.length != 0) {
				String jarName = files[0].getName();
				return jarName;
			}
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "test.jmeter.compilation");
		}	
	
	private void prepareJMeterEnv() throws IOException {
		log.debug("prepareJMeterEnv() - Preparing the JMeter 5.5 enviroment");
		JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES);
		JMeterUtils.setJMeterHome(JMETER_HOME);
		JMeterUtils.initLocale();
		SaveService.loadProperties();
		log.debug("prepareJMeterEnv() - Enviroment ready");
	}
	
	private String executeJMeterTests(List<File> zips, String csvLocation) {
		log.debug("Files: " + zips + " , csvLocation: " + csvLocation);

		try {
			prepareJMeterEnv();
			Long totalTime = 0L;
			for (File test : zips) {
				HashTree testPlanTree = SaveService.loadTree(test);

				// https://stackoverflow.com/questions/24958035/how-to-generate-and-save-to-file-summaryreport-or-any-other-jmeter-report
				Summariser summariser = null;
				String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
				if (summariserName.length() > 0) {
					summariser = new Summariser(summariserName);
				}

				ResultCollector logger = new ResultCollector(summariser);
				logger.setFilename(csvLocation);
				testPlanTree.add(testPlanTree.getArray()[0], logger);

				StandardJMeterEngine jMeter = new StandardJMeterEngine();
				jMeter.configure(testPlanTree);
				log.debug("executeJMeterTests() - Executing JMeter test: " + test.getName());
				Long start = System.currentTimeMillis();
				jMeter.run();
				Long finish = System.currentTimeMillis();
				Long elapsedTime = finish - start;
				
				log.info("executeJMeterTests() - Took " + elapsedTime + " ms executing the test: " + test.getName());
				totalTime += elapsedTime;
			}
			String totalTestTime = DateFormatUtils.format(totalTime, "m'm':ss's'");
			return totalTestTime;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "test.jmeter.execution");
		}
	}
	
	public InputStream downloadTests(String uid) {
		try {
			Project project = projectService.getProjectIfLoggedIsOwner(uid);
			File f = new File(project.getSystemPath() + "/Tests.zip");
			if (f.exists()) {
				Files.delete(f.toPath());
			}
			if (project.isUnitTestExecuted() && project.isPerformanceTestExecuted()) {
				log.debug("downloadTests() - Dowmloading both unit and performance tests of project: " + project.getId());
				String surefireTestsLocation = project.getSystemPath() + "/target/site/surefire-report.html";
				String jMeterTestsLocation = project.getSystemPath() + JMETER_RESULT_FILE;
				String addonLocation = project.getSystemPath() + JS_ADDON_LOCATION;
				Utils.executeCommand(new String[] { "7z", "a", "Tests.zip", surefireTestsLocation, addonLocation, jMeterTestsLocation }, new File(project.getSystemPath()));
				InputStream in = new FileInputStream(new File(project.getSystemPath() + "/Tests.zip"));
				return in;
			} else if (project.isUnitTestExecuted()) {
				log.debug("downloadTests() - Downloading only the unit tests of project:" + project.getId());
				String surefireTestsLocation = project.getSystemPath() + "/target/site/surefire-report.html";
				String addonLocation = project.getSystemPath() + JS_ADDON_LOCATION;
				Utils.executeCommand(new String[] {	"7z", "a", "Tests.zip", surefireTestsLocation, addonLocation }, new File(project.getSystemPath()));
				InputStream in = new FileInputStream(new File(project.getSystemPath() + "/Tests.zip"));
				return in;
			} else if (project.isPerformanceTestExecuted()) {
				log.debug("downloadTests() - Downloading only the performance tests of project: " + project.getId());
				String jMeterTestsLocation = project.getSystemPath() + JMETER_RESULT_FILE;
				Utils.executeCommand(new String[] { "7z", "a", "Tests.zip", jMeterTestsLocation }, new File(project.getSystemPath()));
				InputStream in = new FileInputStream(new File(project.getSystemPath() + "/Tests.zip"));
				return in;
			} else {
				log.warn("downloadTests() - The project hasn't execute any test");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project.not.executed.test");
			}
		} catch (Exception e) {
			log.error("downloadTests() - Preparing the download has raised an error: " + e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}
}

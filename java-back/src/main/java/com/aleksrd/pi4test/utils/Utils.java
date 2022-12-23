
package com.aleksrd.pi4test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.security.entities.UserAccount;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class Utils {

	public static UserAccount getPrincipal() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof String) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		} else {
		return (UserAccount) principal;	
		}
//		return (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public static String getHomeDir() {
		return System.getProperty("user.home");
	}

	public static boolean checkPrincipalAuthority(String authority) {
		if (getPrincipal().getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(authority))) {
			return true;
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user.access.denied");
		}
	}
	
	public static String moveMultipartFileToLocation(final MultipartFile uploadedFile, final String projectName, String shortFilename) {
		try {
			String home = getHomeDir();
			String directory = home + "/" + getPrincipal().getUsername();
			if (uploadedFile.getBytes().length != 0) {
				if (Files.exists(Path.of(directory + "/" + shortFilename))) {
					FileUtils.forceDelete(new File(directory + "/" + shortFilename));
				}
				File location = new File(directory);
				location.mkdir();
				Files.copy(uploadedFile.getInputStream(), Path.of(directory, projectName), StandardCopyOption.REPLACE_EXISTING);
				return directory;
			} else {
				String msg = "The file has a length of 0";
				log.error("moveMultipartFileToLocation()" + msg);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file.upload.error");
			}
		} catch (IOException e) {
			String msg = "Error during the transfer of the file to the filesystem";
			log.error("moveMultipartFileToLocation() - " + msg);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	public static Process executeCommand(final String[] command, File location) throws ResponseStatusException {
		try {
			log.info("executeCommand() - Executing: " + Arrays.asList(command).stream().collect(Collectors.joining(", ")));
			Process p = Runtime.getRuntime().exec(command, null, location);

			BufferedReader noErrorReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String noErrorline;
			while ((noErrorline = noErrorReader.readLine()) != null) {
				if (log.isDebugEnabled()) {
					log.debug(noErrorline);
				}
			}

			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String errorLine;
			while ((errorLine = errorReader.readLine()) != null) {
				log.warn(errorLine);
			}

			p.waitFor();

			if (command[1].contains("upload")) {
				if (p.exitValue() == 1) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "project.not.zip");
				} else if (p.exitValue() == 2) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project.not.maven");
				}
			}

			if (p.exitValue() != 0) {
				throw new Exception();
			}
			return p;

		} catch (Exception e) {
			if (e instanceof ResponseStatusException) {
				throw (ResponseStatusException) e;
			}

			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	public static String readFile(final String fileLocaltion, String fileToRead) {

		File f = new File(fileLocaltion + fileToRead);
		BufferedReader bf;
		String res = "";
		try {
			bf = new BufferedReader(new FileReader(f));
			res = bf.readLine();
			bf.close();
			Files.delete(f.toPath());
			return res;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	// https://www.baeldung.com/spring-classpath-file-access#2-reading-as-aninputstream
	public static String chargeScript(String fileName) {
		try {
			String filenameWithoutExtension = fileName.substring(0, fileName.length() - 3);
			File tempFolder = new File(System.getProperty("java.io.tmpdir"));
			File[] matches = tempFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {

					return name.startsWith(filenameWithoutExtension);
				}
			});

			if (matches.length == 0) {
				InputStream resource = new ClassPathResource("/scripts/" + fileName).getInputStream();
				File tmpFile = File.createTempFile(fileName, null);
				Files.copy(resource, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				tmpFile.deleteOnExit();
				return tmpFile.getAbsolutePath();
			}
			// If file is in /tmp directory, then we use it
			return matches[0].getPath();
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}
}

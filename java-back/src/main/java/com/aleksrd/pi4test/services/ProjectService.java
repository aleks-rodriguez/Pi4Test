
package com.aleksrd.pi4test.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.projects.ProjectListResponseDto;
import com.aleksrd.pi4test.entities.Project;
import com.aleksrd.pi4test.repositories.ProjectRepository;
import com.aleksrd.pi4test.security.entities.UserAccount;
import com.aleksrd.pi4test.security.services.UserAccountService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@Service
@Transactional
@CommonsLog
public class ProjectService  {
	
	private final String UPLOAD_SCRIPT = "upload.sh";
	private final String DROP_DB_SCRIPT = "dropDatabase.sh";
	
	@Autowired
	private ProjectRepository	projectRepository;
	@Autowired
	private UserAccountService accountService;
	@Autowired
	private TestsService testService;
	
	public Project getProjectIfLoggedIsOwner(String uidProject) {
		List<Project> loggedUserProjects = findAllByUser();
		Project p = findOneByUserAndUid(uidProject);
		if (p == null || !loggedUserProjects.contains(p)) {
			log.error("The project: " + p.getTitle() + " doesn't belong to the user " + Utils.getPrincipal().getUsername());
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "project.not.yours");
		}
		return p;
	}

	public Project findOneByUserAndUid(String uidProject) {
		UserAccount logged = accountService.findByUserName(Utils.getPrincipal().getUsername());
		log.debug("findOneByUser() - Retrieving project with uid: " + uidProject);
		return projectRepository.findOneByUidAndAccountId(logged.getId(), uidProject).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project.not.found"));
	}
	
	public List<ProjectListResponseDto> findAllDtoByUser() {
		UserAccount logged = accountService.findByUserName(Utils.getPrincipal().getUsername());
		log.debug("listAllByLoggedUser() - Retrieving all projects of user: " + Utils.getPrincipal().getUsername());
		List<Project> projectsByUser = projectRepository.findAllByAccountId(logged.getId());
		List<ProjectListResponseDto> dtos = projectsByUser.stream().map(x -> toDto(x)).collect(Collectors.toList());
		return dtos;
	}
	public List<Project> findAllByUser() {
		UserAccount logged = accountService.findByUserName(Utils.getPrincipal().getUsername());
		log.debug("listAllByLoggedUser() - Retrieving all projects of user: " + Utils.getPrincipal().getUsername());
		List<Project> projectsByUser = projectRepository.findAllByAccountId(logged.getId());
		return projectsByUser;
	}

	public Project save(final String title, String description, String path) throws ResponseStatusException {
		Project toSave = new Project();
		toSave.setTitle(title);
		toSave.setDescription(description);
		toSave.setSystemPath(path);
		toSave.setUnitTestExecuted(false);
		toSave.setPerformanceTestExecuted(false);
		toSave.setUidProject(UUID.randomUUID().toString());
		try {
			UserAccount logged = accountService.findByUserName(Utils.getPrincipal().getUsername());
			log.info("save() - Saving project: " + title + " to user: " + logged.getUsername());
			toSave.setAccount(logged);
			Project saved = projectRepository.save(toSave);
			projectRepository.flush();
			return saved;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	public ProjectListResponseDto update(Project update) {
		log.debug("update() - Updating project: " + update.getTitle());
		Project updated = projectRepository.save(update);
		return toDto(updated);
	}

	public void delete(String projectUid) {
		try {
			Project toDelete = getProjectIfLoggedIsOwner(projectUid);
			log.debug("delete() - Deleting project: " + toDelete.getTitle());

			FileUtils.deleteDirectory(new File(toDelete.getSystemPath()));
			testService.deleteTest(toDelete.getId());
			projectRepository.delete(toDelete);
			if(toDelete.getDatabaseName() != null) {
				String dropDbScriptLocation = Utils.chargeScript(DROP_DB_SCRIPT);
				Utils.executeCommand(new String[] {"/bin/bash", dropDbScriptLocation, toDelete.getDatabaseName() }, new File("/"));	
			}
		} catch (IOException e) {
			log.error("delete() - Unable to delete the project files from the filesystem");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "project.delete.error");
		}
	}

	public void deleteAllFromUser(List<Project> userProjects) {
		try {
			String dropDbScriptLocation = Utils.chargeScript(DROP_DB_SCRIPT);
			for(Project toDelete: userProjects) {
				FileUtils.deleteDirectory(new File(toDelete.getSystemPath()));
				if (toDelete.getDatabaseName() != null) {
					Utils.executeCommand(new String[] { "/bin/bash", dropDbScriptLocation, toDelete.getDatabaseName() }, new File("/"));	
				}
			}
			projectRepository.deleteAll(userProjects);
			projectRepository.flush();
		}catch(Exception e) {
			log.error("deleteAll() - Error while deleting the projects: ", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "project.delete.error");
		}
	}

	public ProjectListResponseDto handleUpload(final MultipartFile file) throws ResponseStatusException {
		String filename = file.getOriginalFilename();
		String shortFilename = filename.substring(0, filename.length() - 4);
		String location = Utils.moveMultipartFileToLocation(file, filename, shortFilename); // Paso el filename para que
																							// asi cree el zip en el
																							// nuevo lugar
		String uploadPath = Utils.chargeScript(UPLOAD_SCRIPT);
		Utils.executeCommand(new String[] { "/bin/bash", uploadPath, shortFilename }, new File(location));
		String pathToFolder = location + "/" + shortFilename;
		String projectTitle = Utils.readFile(pathToFolder, "/name.txt");
		projectTitle = sanitizeString(projectTitle);
		String projectDesc = Utils.readFile(pathToFolder, "/desc.txt");
		projectDesc = sanitizeString(projectDesc);
		Utils.executeCommand(new String[] { "mvn", "clean" }, new File(pathToFolder));

		UserAccount userAccount = accountService.findByUserName(Utils.getPrincipal().getUsername());
		Project exist = projectRepository.findOneByTitleAndAccountId(userAccount.getId(), projectTitle).orElse(null);
		if (exist != null) {
			exist.setTitle(projectTitle);
			exist.setDescription(projectDesc);
			return update(exist);
		} else {
			Project saved = save(projectTitle, projectDesc, pathToFolder);
			return toDto(saved);
		}
	}
	
	private String sanitizeString(String toReplace) {
		String sanitized = "";
		sanitized = toReplace.replaceAll("<", "");
		sanitized = sanitized.replaceAll(">", "");
		sanitized = sanitized.replaceAll("&amp;", "&");
		
		return sanitized;
	}
	
	private ProjectListResponseDto toDto(Project entity) {
		ProjectListResponseDto dto = new ProjectListResponseDto(entity.getUidProject(), entity.getTitle(), entity.getDescription(), entity.isUnitTestExecuted(), entity.isPerformanceTestExecuted());
		return dto;
	}
}

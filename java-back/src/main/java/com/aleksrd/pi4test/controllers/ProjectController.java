
package com.aleksrd.pi4test.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.projects.ProjectListResponseDto;
import com.aleksrd.pi4test.services.ProjectService;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping("/api/project")
@CommonsLog
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;

	@GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProjectListResponseDto>> listAllByLoggedUser() {
		log.info("GET /api/project/list");
		List<ProjectListResponseDto> projects = projectService.findAllDtoByUser();
		return new ResponseEntity<List<ProjectListResponseDto>>(projects, HttpStatus.OK);
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<ProjectListResponseDto>> upload(@RequestBody MultipartFile file) throws ResponseStatusException {
		log.info("POST /api/project/upload");
		if (!file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).equals("zip")) {
			log.info("upload() - The extension of the uploaded file is not \".zip\"");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project.not.zip");
		}
		log.info("upload() - Uploading file: " + file.getOriginalFilename());
		ProjectListResponseDto response = projectService.handleUpload(file);
		return new ResponseEntity<List<ProjectListResponseDto>>(Arrays.asList(response), HttpStatus.OK);
	}

	@DeleteMapping(value = "/delete/{uid}")
	public ResponseEntity<Void> delete(@PathVariable String uid) {
		log.info("DELETE /api/project/delete/");
		projectService.delete(uid);
		return ResponseEntity.ok().build();
	}

}

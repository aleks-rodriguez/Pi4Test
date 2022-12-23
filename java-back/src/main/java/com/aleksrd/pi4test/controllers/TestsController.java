
package com.aleksrd.pi4test.controllers;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aleksrd.pi4test.dto.test.JMeterTestRequestDto;
import com.aleksrd.pi4test.dto.test.UnitTestRequestDto;
import com.aleksrd.pi4test.services.TestsService;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping("/api/test")
@CommonsLog
public class TestsController {

	@Autowired
	private TestsService testsService;

	@PostMapping(value = "/runUnit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> runUnitTests(@ModelAttribute UnitTestRequestDto dto, BindingResult binding) throws Exception {
		log.info("POST - /api/test/runUnit");
		testsService.prepareUnitTest(dto);

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/getJMeterPort")
	public ResponseEntity<Integer> getRandomJMeterTestsPort() {
		Integer port = testsService.getRandomPort();
		return new ResponseEntity<Integer>(port, HttpStatus.OK);
	}
	
	@PostMapping(value = "/jmeter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> runPerformanceTests(@ModelAttribute JMeterTestRequestDto dto, BindingResult binding) {
		log.info("POST - /api/test/jmeter");
		testsService.doPerformanceTests(dto);

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/download/{uid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> downloadTests(@PathVariable String uid) throws Exception {
		log.info("GET /api/test/download");
		InputStream downloadZipStream = testsService.downloadTests(uid);
		HttpHeaders header = new HttpHeaders();
		ContentDisposition cont = ContentDisposition.attachment().build();
		header.setContentDisposition(cont);
		header.setCacheControl("no-cache, no-store, must-revalidate");
		header.setPragma("no-cache");
		header.setExpires(0);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.ok().headers(header).body(new InputStreamResource(downloadZipStream));
	}
}

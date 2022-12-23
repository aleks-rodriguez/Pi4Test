
package com.aleksrd.pi4test.controllers.admin;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.admin.NewSecurityMessageRequestDto;
import com.aleksrd.pi4test.entities.SecurityMessage;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.services.SecurityMessageService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping("/api/admin/message")
@CommonsLog
public class SecurityMessageController {

	@Autowired
	private SecurityMessageService messageService;


	@GetMapping(value = "get")
	public ResponseEntity<List<SecurityMessage>> getAdviceMessages() {
		log.info("GET /api/admin/message/get");
		return new ResponseEntity<List<SecurityMessage>>(messageService.existAnyMessage(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasRole('ADMIN')")
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SecurityMessage>> saveSecurityMessage(@Valid @RequestBody NewSecurityMessageRequestDto dto, BindingResult binding) {
		log.info("POST /api/admin/message/save");
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		Utils.checkPrincipalAuthority(Role.ROLE_ADMIN.toString());
		SecurityMessage message = messageService.save(dto);

		return new ResponseEntity<List<SecurityMessage>>(Arrays.asList(message), HttpStatus.OK);
	}
	
	@PreAuthorize(value = "hasRole('ADMIN')")
	@DeleteMapping(value = "/delete/{uid}")
	public ResponseEntity<Void> resetMessage(@PathVariable("uid") String uid) {
		log.info("DELETE /api/admin/message/delete");
		Utils.checkPrincipalAuthority(Role.ROLE_ADMIN.toString());
		messageService.delete(uid);
		
		return ResponseEntity.ok().build();

	}

}

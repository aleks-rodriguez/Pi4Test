
package com.aleksrd.pi4test.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.security.dto.NewUserDto;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.security.services.UserAccountService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping(value = "/api/admin/users")
@CommonsLog
public class NewAdministratorController {
	@Autowired
	private UserAccountService	userAccountService;


	@PreAuthorize(value = "hasRole('ADMIN')")
	@PostMapping(value = "/newAdmin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> newAdmin(@RequestBody NewUserDto newAdmin, BindingResult binding) {
		log.info("POST /api/auth/newAdmin");
		Utils.checkPrincipalAuthority(Role.ROLE_ADMIN.toString());
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (userAccountService.existsByUserName(newAdmin.nick())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The username already exists");
		}
		log.debug("newAdmin() - Creating new admin: " + newAdmin.nick());
		userAccountService.createAdmin(newAdmin);
		return ResponseEntity.ok().build();
	}

}

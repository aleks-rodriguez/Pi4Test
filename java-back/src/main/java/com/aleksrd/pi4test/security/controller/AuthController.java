
package com.aleksrd.pi4test.security.controller;

import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.RefreshJwtRequestDto;
import com.aleksrd.pi4test.dto.myData.ChangePassRequestDto;
import com.aleksrd.pi4test.security.dto.JwtDto;
import com.aleksrd.pi4test.security.dto.NewUserDto;
import com.aleksrd.pi4test.security.dto.UserLoginDto;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.security.services.UserAccountService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping(value = "/api/auth")
@CommonsLog
public class AuthController {

	@Autowired
	private UserAccountService userAccountService;


	@PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JwtDto> newUser(@Valid @RequestBody NewUserDto newUser, BindingResult binding) {
		log.info("POST /api/auth/new");
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "auth.user.form.error");
		}
		if (userAccountService.existsByUserName(newUser.nick())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "auth.new.user.exists");
		}

		if (!(newUser.password().equals(newUser.repeatPass()))) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "auth.new.user.distinct.pass");
		}

		log.info("newUser() - Creating user: " + newUser.nick());
		try {
			JwtDto login = userAccountService.createUser(newUser);
			return new ResponseEntity<JwtDto>(login, HttpStatus.OK);
		} catch (Exception e) {
			log.error("newUser() - An error has ocurred: " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	@PreAuthorize(value = "hasRole('ADMIN')")
	@PostMapping(value = "/newAdmin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JwtDto> newAdmin(@RequestBody NewUserDto newUser, BindingResult binding) {
		log.info("POST /api/auth/newAdmin");
		Utils.checkPrincipalAuthority(Role.ROLE_ADMIN.toString());
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "auth.user.form.error");
		}
		if (userAccountService.existsByUserName(newUser.nick())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "auth.new.user.exists");
		}

		log.info("newAdmin() - Creating new admin: " + newUser.nick());
		try {
			userAccountService.createAdmin(newUser);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("newUser() - An error has ocurred: " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JwtDto> login(@Valid @RequestBody UserLoginDto userLogin, BindingResult binding) {
		log.info("POST /api/auth/login");
		if (binding.hasErrors()) {
			log.warn("login() - Wrong user name or password");
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "auth.user.form.error");
		}
		log.info("login() - Login user: " + userLogin.nick());
		try {
			JwtDto jwtDto = userAccountService.loginUser(userLogin);
			return new ResponseEntity<JwtDto>(jwtDto, HttpStatus.OK);

		} catch (BadCredentialsException ex) {
			log.error("login() - Ha ocurrido un error: " + ex.getMessage());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "auth.wrong.credentials");
		}
	}

	@GetMapping(value = "/logout")
	public ResponseEntity<Void> logout() throws AuthenticationException {
		log.info("GET /api/auth/logout");
		userAccountService.logoutUser();
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(value = "/refresh")
	public ResponseEntity<JwtDto> refresh(@RequestBody RefreshJwtRequestDto oldJwt) {
		log.debug("POST /api/auth/refresh");
		JwtDto jwt = userAccountService.refreshToken(oldJwt.token());
		return new ResponseEntity<JwtDto>(jwt, HttpStatus.OK);
	}
	
	@PostMapping(value = "/changePass")
	public ResponseEntity<Void> changePass(@RequestBody ChangePassRequestDto dto, BindingResult binding) {
		log.info("POST /api/users/changePass");
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.equals(dto.password(), dto.repeatPass())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "auth.new.user.distinct.pass");
		}
		userAccountService.changePassword(dto.password());
		return ResponseEntity.ok().build();
	}
}

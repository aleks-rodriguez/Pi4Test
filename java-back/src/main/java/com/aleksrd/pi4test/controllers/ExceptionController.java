
package com.aleksrd.pi4test.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.services.exceptions.APIException;

@RestControllerAdvice
public class ExceptionController {

	@Autowired
	private MessageSource message;


	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<APIException> handleResponseStatus(ResponseStatusException oops) {
		Locale locale = LocaleContextHolder.getLocale();
		String mess = "";
		if(oops.getMessage().equals("401 UNAUTHORIZED")) {
			mess = "user.access.denied";
		}else {
			mess = oops.getReason();
		}
		APIException ex = new APIException(message.getMessage(mess, null, locale));
		return ResponseEntity.status(oops.getStatus()).body(ex);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<APIException> userNotFound(UsernameNotFoundException oops) {
		Locale locale = LocaleContextHolder.getLocale();
		APIException ex = new APIException(message.getMessage(oops.getMessage(), null, locale));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<APIException> accessDenied(AccessDeniedException oops){
		Locale locale = LocaleContextHolder.getLocale();
		APIException ex = new APIException(message.getMessage("user.access.denied", null, locale));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex);
	}

	//Internal errors, no message to the user
	@ExceptionHandler(InternalServerError.class)
	public ResponseEntity<Void> handleInternalServer(InternalServerError oops) {
		return ResponseEntity.internalServerError().build();
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Void> requestMethodNotSupported(HttpRequestMethodNotSupportedException oops) {
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Void> mediaTypeNotSupported(HttpMediaTypeNotSupportedException oops) {
		return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<Void> missingRequestHeeader(MissingRequestHeaderException oops) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Void> handleNullPointer(NullPointerException oops) {
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

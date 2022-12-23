package com.aleksrd.pi4test.security.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public record NewUserDto(
		@NotBlank @Length(min = 2, max = 20) String nick,
		@NotBlank @Length(min = 8) @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*") String password,
		@NotBlank @Length(min = 8) @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*") String repeatPass,
		Set<String> roles
		) {
}

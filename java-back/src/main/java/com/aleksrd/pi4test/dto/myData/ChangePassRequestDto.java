package com.aleksrd.pi4test.dto.myData;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public record ChangePassRequestDto(
		@NotBlank @Length(min = 8) @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*") String password,
        @NotBlank @Length(min = 8) @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z]).*") String repeatPass) {

}

package com.aleksrd.pi4test.security.dto;

import javax.validation.constraints.NotBlank;

public record UserLoginDto(@NotBlank String nick,@NotBlank String password) {
}

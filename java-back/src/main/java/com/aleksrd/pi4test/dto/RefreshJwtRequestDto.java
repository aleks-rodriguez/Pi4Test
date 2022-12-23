package com.aleksrd.pi4test.dto;

import javax.validation.constraints.NotBlank;

public record RefreshJwtRequestDto(@NotBlank String token) {

}

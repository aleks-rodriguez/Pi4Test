
package com.aleksrd.pi4test.security.dto;

import javax.validation.constraints.NotBlank;

public record JwtDto(@NotBlank String token) {
}

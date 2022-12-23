
package com.aleksrd.pi4test.dto.admin;

import javax.validation.constraints.NotBlank;

public record NewSecurityMessageRequestDto(@NotBlank String title, @NotBlank String spMessage, @NotBlank String enMessage) {

}


package com.aleksrd.pi4test.dto.test;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record JMeterTestRequestDto(@NotNull MultipartFile zipFile, @NotNull String projectUid, Integer deployPort) {

}

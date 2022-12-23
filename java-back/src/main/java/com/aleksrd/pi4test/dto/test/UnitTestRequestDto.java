package com.aleksrd.pi4test.dto.test;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record UnitTestRequestDto(@NotNull String dbName, @NotNull MultipartFile sqlFile,  @NotNull String projectUid) {

}

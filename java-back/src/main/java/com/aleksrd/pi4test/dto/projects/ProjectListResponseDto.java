
package com.aleksrd.pi4test.dto.projects;

public record ProjectListResponseDto(String uid, String title, String description, Boolean unitTestExecuted, Boolean performanceTestExecuted) {

}

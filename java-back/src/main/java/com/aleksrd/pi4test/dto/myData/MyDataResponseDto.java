
package com.aleksrd.pi4test.dto.myData;

import java.util.List;

import com.aleksrd.pi4test.dto.test.TestsMyDataResponseDto;
import com.aleksrd.pi4test.entities.Project;

public record MyDataResponseDto(String userName, String createDate, List<Project> projects, List<TestsMyDataResponseDto> tests) {

}

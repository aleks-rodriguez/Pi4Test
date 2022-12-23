package com.aleksrd.pi4test.dto.admin.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MinMaxAvgStdTestsDbDTO {
	private Double min;
	private Double max;
	private Double avg;
	private Double stdDev;
	

}

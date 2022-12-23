
package com.aleksrd.pi4test.dto.admin.statistics;

public record TestStatisticsDto(Double maxTestPerProject, String projectsWithMoreTests, Double minTestPerProject, String projectsWithLessTests, Double avgTestsPerProject, Double stdDevTestPerProject, String top3UsersTest) {
}

package com.aleksrd.pi4test.dto.admin.statistics;


public record ProjectStatisticsDto(Double maxProjectsPerUser, String usersWithMoreProjects, Double minProjectsPerUser, String usersWithLessProjects, Double avgProjectsPerUser, Double stdDevProjectsPerUser) {

}

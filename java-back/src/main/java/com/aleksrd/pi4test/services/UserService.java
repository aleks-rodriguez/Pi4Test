
package com.aleksrd.pi4test.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.admin.statistics.AppStatisticsResponseDto;
import com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdProjectDbDTO;
import com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdTestsDbDTO;
import com.aleksrd.pi4test.dto.admin.statistics.ProjectStatisticsDto;
import com.aleksrd.pi4test.dto.admin.statistics.TestStatisticsDto;
import com.aleksrd.pi4test.dto.admin.statistics.UsersStatisticsDto;
import com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto;
import com.aleksrd.pi4test.dto.myData.MyDataResponseDto;
import com.aleksrd.pi4test.dto.test.TestsMyDataResponseDto;
import com.aleksrd.pi4test.entities.Project;
import com.aleksrd.pi4test.entities.Testing;
import com.aleksrd.pi4test.entities.UserStatistics;
import com.aleksrd.pi4test.repositories.UserStatsRepository;
import com.aleksrd.pi4test.security.entities.UserAccount;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.security.services.UserAccountService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@Service
@Transactional
@CommonsLog
public class UserService {

	@Autowired
	private UserStatsRepository	userStatsRepository;
	@Autowired
	private UserAccountService	accountService;
	@Autowired
	private ProjectService		projectService;
	@Autowired
	private TestsService		testsService;


	public void save(UserStatistics toSave) {
		userStatsRepository.save(toSave);
	}

	public void updateTotalRegisteredUsers(String userName) {
		UserStatistics stat = userStatsRepository.findAll().get(0);
		stat.getTotalRegisteredUsers().add(userName);
		save(stat);
	}

	public void updateTotalDeletedUsers(String userName) {
		UserStatistics stat = userStatsRepository.findAll().get(0);
		stat.getTotalDeletedUsers().add(userName);
		save(stat);
	}

	public MyDataResponseDto getAllMyData() {
		UserAccount logged = Utils.getPrincipal();
		
		log.debug("getAllMyData() - Retrieving all data of user: " + logged.getUsername());
		List<Project> projects = projectService.findAllByUser();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String createdAt = logged.getCreatedAt().format(dtf);

		List<Testing> dbTests = new ArrayList<>();
		for (Project project : projects) {
			dbTests.addAll(testsService.findAllByProject(project.getId()));
		}

		List<TestsMyDataResponseDto> responseTests = dbTests.stream().map(x -> toDto(x, dtf)).collect(Collectors.toList());

		MyDataResponseDto myData = new MyDataResponseDto(logged.getUsername(), createdAt, projects, responseTests);
		return myData;
	}

	public void deleteAccount() {
		UserAccount account = accountService.findByUserName(Utils.getPrincipal().getUsername());
		
		log.debug("deleteAccount() - Deleting " + account.getUsername() + " account...");

		if (account.getRole().equals(Role.ROLE_TESTER)) {
			List<Project> userProjects = projectService.findAllByUser();

			for (Project project : userProjects) {
				testsService.deleteTest(project.getId());
			}
			projectService.deleteAllFromUser(userProjects);
			accountService.delete(account);

			updateTotalDeletedUsers(account.getUsername());

		} else if (account.getRole().equals(Role.ROLE_ADMIN)) {
			if (account.isSuperAdmin()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "auth.no.superadmin.delete");
			} else {
				accountService.delete(account);
				updateTotalDeletedUsers(account.getUsername());
			}
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	public AppStatisticsResponseDto getUserStats() {
		UserStatistics statistics = userStatsRepository.findAll().get(0);

		log.debug("getUserStats() - Retrieving user statistics");
		Integer actualRegisteredUsers = statistics.getActualRegisteredUsers();
		List<String> totaRegisteredUsers = statistics.getTotalRegisteredUsers();
		List<String> totalDeletedUsers = statistics.getTotalDeletedUsers();
		List<String> bannedUsers = accountService.findBanned();

		UsersStatisticsDto users = new UsersStatisticsDto(actualRegisteredUsers, listToStringConverter(totaRegisteredUsers), listToStringConverter(totalDeletedUsers), listToStringConverter(bannedUsers));

		log.debug("getUserStats() - Retrieving projects statistics");
		MinMaxAvgStdProjectDbDTO minMaxAvgStdDevOfProjectsPerAccount = userStatsRepository.minMaxAvgStdDevOfProjectsPerAccount();
		Double maxProjectsPerUser = minMaxAvgStdDevOfProjectsPerAccount.getMax();
		List<String> usersWithMoreProjects = userStatsRepository.findUsersWithMoreProjects();
		Double minProjectsPerUser = minMaxAvgStdDevOfProjectsPerAccount.getMin();
		List<String> usersWithLessProjects = userStatsRepository.findUsersWithLessProjects();
		Double avgProjectsPerUser = minMaxAvgStdDevOfProjectsPerAccount.getAvg();
		Double stdDevProjectsPerUser = minMaxAvgStdDevOfProjectsPerAccount.getStdDev();

		ProjectStatisticsDto projects = new ProjectStatisticsDto(maxProjectsPerUser, listToStringConverter(usersWithMoreProjects), minProjectsPerUser, listToStringConverter(usersWithLessProjects), avgProjectsPerUser, stdDevProjectsPerUser);

		log.debug("getUserStats() - Retrieving tests statistics");
		MinMaxAvgStdTestsDbDTO minMaxAvgStdDevOfTestsPerProject = userStatsRepository.minMaxAvgStdDevOfTestsPerProjects();
		Double maxTestPerProject = minMaxAvgStdDevOfTestsPerProject.getMax();

		List<String> projectsWithMoreTests = userStatsRepository.findProjecstWithMoreTest();
		Double minTestPerProject = minMaxAvgStdDevOfTestsPerProject.getMin();
		List<String> projectsWithLessTests = userStatsRepository.findProjecstWithLessTest();
		Double avgTestsPerProject = minMaxAvgStdDevOfTestsPerProject.getAvg();
		Double stdDevTestPerProject = minMaxAvgStdDevOfTestsPerProject.getStdDev();

		List<String> top3 = userStatsRepository.findTop3UsersWithMoreTests();
		if (top3.size() > 3) {
			top3 = top3.subList(0, 2);
		}

		TestStatisticsDto tests = new TestStatisticsDto(maxTestPerProject, listToStringConverter(projectsWithMoreTests), minTestPerProject, listToStringConverter(projectsWithLessTests), avgTestsPerProject, stdDevTestPerProject,
			listToStringConverter(top3));

		AppStatisticsResponseDto appStatistics = new AppStatisticsResponseDto(users, projects, tests);

		return appStatistics;

	}

	public List<AllUsersDbResponseDto> getAllUsersFromSystem() {
		log.debug("getAllUsersFromSystem() - Getting all users...");
		return accountService.findAllUsers();
	}
	
	public List<AllUsersDbResponseDto> getOtherAdminsFromSystem() {
		log.debug("getAllUsersFromSystem() - Getting all admins without logged one...");
		String loggedUidAccount = Utils.getPrincipal().getUidAccount();
		return accountService.findAllOtherAdmins(loggedUidAccount);
	}

	public void banUser(String bannedUid) {
		accountService.banUser(bannedUid);
	}

	public void enableUser(String bannedUid) {
		accountService.enableUser(bannedUid);
	}
	
	public void convertToSuperAdmin(String newSuperAdminUid) {
		UserAccount logged = Utils.getPrincipal();
		if(logged.isSuperAdmin()) {
			UserAccount newSuperAdmin = accountService.findByUidAccount(newSuperAdminUid);
			newSuperAdmin.setSuperAdmin(true);
			logged.setSuperAdmin(false);
			accountService.saveAll(Arrays.asList(logged, newSuperAdmin));
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user.access.denied");
		}
	}

	private TestsMyDataResponseDto toDto(Testing test, DateTimeFormatter dtf) {
		String executedAt = test.getExecutedAt().format(dtf);
		return new TestsMyDataResponseDto(test.getProject().getTitle(), test.getUidTest(), test.getType().toString(), test.getElapsedTime(), executedAt);
	}

	private String listToStringConverter(List<String> ls) {
		return ls.stream().collect(Collectors.joining(" "));
	}
}


package com.aleksrd.pi4test;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aleksrd.pi4test.entities.Project;
import com.aleksrd.pi4test.entities.UserStatistics;
import com.aleksrd.pi4test.repositories.ProjectRepository;
import com.aleksrd.pi4test.repositories.SecurityMessageRepository;
import com.aleksrd.pi4test.repositories.TestsRepository;
import com.aleksrd.pi4test.repositories.UserStatsRepository;
import com.aleksrd.pi4test.security.entities.UserAccount;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.security.repositories.UserAccountRepository;

import lombok.extern.apachecommons.CommonsLog;

//@Component ONLY FOR DEVELOMENT
@CommonsLog
public class DatabaseInitializator implements CommandLineRunner {

	@Autowired
	private UserAccountRepository	userAccountRepo;
	@Autowired
	private PasswordEncoder			passwordEncoder;
	@Autowired
	private ProjectRepository		projectRepository;
	@Autowired
	private TestsRepository			testRepository;
	@Autowired
	private UserStatsRepository		userStatsRepository;
	@Autowired
	private SecurityMessageRepository securityMessageRepository;


	@Override
	public void run(String... args) throws Exception {
		testRepository.deleteAllInBatch();
		projectRepository.deleteAllInBatch();
		List<UserStatistics> allStats = userStatsRepository.findAll();
		if(!allStats.isEmpty()) {
			userStatsRepository.delete(allStats.get(0));
		}
		
		userStatsRepository.deleteAll();
		userAccountRepo.deleteAllInBatch();
		securityMessageRepository.deleteAllInBatch();

		UserAccount admin = new UserAccount("TestAdmin", passwordEncoder.encode("TestP@$$123"), Role.ROLE_ADMIN);
		admin.setSuperAdmin(true);
		userAccountRepo.save(admin);

		UserAccount user = new UserAccount("TestUser", passwordEncoder.encode("TestP@$$123"), Role.ROLE_TESTER);
		userAccountRepo.save(user);

		UserStatistics stats = new UserStatistics();
		//There are a minimum of two users: TestAdmin and TestUser
		stats.getTotalRegisteredUsers().add("TestAdmin");
		stats.getTotalRegisteredUsers().add("TestUser");
		userStatsRepository.save(stats);
		
		Project mock = new Project();
		mock.setAccount(user);
		mock.setDescription("Demo Project");
		mock.setPerformanceTestExecuted(true);
		mock.setSystemPath("/home/alekks/TestUser/DEMO/");
		mock.setTitle("Mock");
		mock.setUidProject(UUID.randomUUID().toString());
		mock.setUnitTestExecuted(true);
		projectRepository.save(mock);

		log.info("Database initialitation complete");
	}

}

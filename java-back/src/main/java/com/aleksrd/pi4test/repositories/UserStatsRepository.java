
package com.aleksrd.pi4test.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdProjectDbDTO;
import com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdTestsDbDTO;
import com.aleksrd.pi4test.entities.UserStatistics;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStatistics, Integer> {

	@Query("select new com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdProjectDbDTO(min(1.0*(select count(p) from Project p where p.account.id = a.id)), max(1.0*(select count(p) from Project p where p.account.id = a.id)), avg(1.0*(select count(p) from Project p where p.account.id = a.id)), stddev(1.0*(select count(p) from Project p where p.account.id = a.id))) from UserAccount a where a.role='ROLE_TESTER'")
	MinMaxAvgStdProjectDbDTO minMaxAvgStdDevOfProjectsPerAccount();

	@Query("select p.account.userName from Project p group by p.account.userName order by count(p) DESC")
	List<String> findUsersWithMoreProjects();

	@Query("select p.account.userName from Project p group by p.account.userName order by count(p) ASC")
	List<String> findUsersWithLessProjects();

	@Query("select new com.aleksrd.pi4test.dto.admin.statistics.MinMaxAvgStdTestsDbDTO(min(1.0*(select count(t) from Testing t where t.project.id = p.id)), max(1.0*(select count(t) from Testing t where t.project.id = p.id)), avg(1.0*(select count(t) from Testing t where t.project.id = p.id)), stddev(1.0*(select count(t) from Testing t where t.project.id = p.id))) from Project p")
	MinMaxAvgStdTestsDbDTO minMaxAvgStdDevOfTestsPerProjects();

	@Query("select t.project.title from Testing t group by t.project.title order by count(t) DESC")
	List<String> findProjecstWithMoreTest();

	@Query("select t.project.title from Testing t group by t.project.title order by count(t) ASC")
	List<String> findProjecstWithLessTest();
	
	@Query("select p.account.userName from Testing t join t.project p group by p.account.userName order by count(t) DESC")
	List<String> findTop3UsersWithMoreTests();
	
	
	
}

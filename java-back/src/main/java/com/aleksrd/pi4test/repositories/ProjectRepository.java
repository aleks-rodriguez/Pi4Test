
package com.aleksrd.pi4test.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aleksrd.pi4test.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

	@Query("select p from Project p where p.account.id=?1")
	List<Project> findAllByAccountId(Integer accountId);
	

	@Query("select p from Project p where p.account.id=?1 and p.uidProject=?2 ")
	Optional<Project> findOneByUidAndAccountId(Integer accountId, String projectUid);

	@Query("select p from Project p where p.account.id.id=?1 and p.title=?2 ")
	Optional<Project> findOneByTitleAndAccountId(Integer accountId, String title);

}

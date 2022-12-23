package com.aleksrd.pi4test.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aleksrd.pi4test.entities.Testing;

@Repository
public interface TestsRepository extends JpaRepository<Testing, Integer> {
	
	List<Testing> findAllByProjectId(int projectId);
	
	void deleteByProjectId(Integer projectId);
}

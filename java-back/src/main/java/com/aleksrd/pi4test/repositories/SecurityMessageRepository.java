package com.aleksrd.pi4test.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aleksrd.pi4test.entities.SecurityMessage;

@Repository
public interface SecurityMessageRepository extends JpaRepository<SecurityMessage, Integer> {
	
	@Query("select case when (count(message) > 0) then true else false end from SecurityMessage message")
	boolean existMessage();
	
	void deleteByUidMessage(String uidMessage);
	

}

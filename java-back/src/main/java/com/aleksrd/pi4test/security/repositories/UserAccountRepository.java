
package com.aleksrd.pi4test.security.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto;
import com.aleksrd.pi4test.security.entities.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

	Optional<UserAccount> findByUserName(String userName);
	boolean existsByUserName(String userName);
	Optional<UserAccount> findByUidAccount(String uidAccount);
	
	@Query("select a.userName from UserAccount a where a.enabled = false")
	List<String> totalBannedUsers();
	
	@Query("select new com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto(a.uidAccount, a.userName, a.enabled, a.superAdmin) from UserAccount a where a.role = 'ROLE_TESTER'")
	List<AllUsersDbResponseDto> findAllUsers();
	
	@Query("select new com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto(a.uidAccount, a.userName, a.enabled, a.superAdmin) from UserAccount a where a.role = 'ROLE_ADMIN' and a.uidAccount <> ?1")
	List<AllUsersDbResponseDto> findAllAdmins(String loggedUidAccount);
}

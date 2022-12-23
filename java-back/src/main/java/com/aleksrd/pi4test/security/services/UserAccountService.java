
package com.aleksrd.pi4test.security.services;

import java.text.ParseException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto;
import com.aleksrd.pi4test.security.dto.JwtDto;
import com.aleksrd.pi4test.security.dto.NewUserDto;
import com.aleksrd.pi4test.security.dto.UserLoginDto;
import com.aleksrd.pi4test.security.entities.UserAccount;
import com.aleksrd.pi4test.security.enums.Role;
import com.aleksrd.pi4test.security.jwt.JwtProvider;
import com.aleksrd.pi4test.security.repositories.UserAccountRepository;
import com.aleksrd.pi4test.services.UserService;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@Service
@Transactional
@CommonsLog
public class UserAccountService implements UserDetailsService {

	@Autowired
	private UserAccountRepository	accountRepository;
	@Autowired
	private JwtProvider				jwtProvider;
	@Autowired
	private PasswordEncoder			passwordEncoder;
	@Autowired
	private UserService				userService;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByUserName(username);
	}

	public UserAccount findByUserName(String userName) {
		return accountRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("auth.user.not.found"));

	}

	public UserAccount findByUidAccount(String uidAccount) {
		return accountRepository.findByUidAccount(uidAccount).orElseThrow(() -> new UsernameNotFoundException("auth.user.not.found"));

	}

	public List<AllUsersDbResponseDto> findAllUsers() {
		return accountRepository.findAllUsers();
	}

	public List<AllUsersDbResponseDto> findAllOtherAdmins(String loggedUidAccount) {
		return accountRepository.findAllAdmins(loggedUidAccount);
	}

	public List<String> findBanned() {
		return accountRepository.totalBannedUsers();
	}

	public boolean existsByUserName(String userName) {
		return accountRepository.existsByUserName(userName);
	}

	public UserAccount save(UserAccount user) {
		return accountRepository.save(user);
	}

	public List<UserAccount> saveAll(List<UserAccount> users) {
		return accountRepository.saveAllAndFlush(users);
	}
	
	public void delete(UserAccount account) {
		accountRepository.delete(account);
	}

	public JwtDto createUser(NewUserDto newUser) {
		UserAccount user = new UserAccount(newUser.nick(), passwordEncoder.encode(newUser.password()), Role.ROLE_TESTER);
		UserAccount account = save(user);
		userService.updateTotalRegisteredUsers(account.getUsername());

		UserLoginDto login = new UserLoginDto(account.getUsername(), newUser.password());
		log.info("createUser() - Login new user: " + login.nick());
		JwtDto jwt = jwtProvider.generateUserLoginToken(login);
		return jwt;
	}

	public void createAdmin(NewUserDto newAdmin) {
		UserAccount user = new UserAccount(newAdmin.nick(), passwordEncoder.encode(newAdmin.password()), Role.ROLE_ADMIN);
		UserAccount account = save(user);
		userService.updateTotalRegisteredUsers(account.getUsername());
	}

	public JwtDto loginUser(UserLoginDto login) {
		return jwtProvider.generateUserLoginToken(login);
	}

	public void logoutUser() {
		try {
			String user = Utils.getPrincipal().getUsername();
			SecurityContextHolder.clearContext();
			log.info("logout() - Successful logout of user " + user);
		} catch (Exception e) {
			log.error("An error has occured while logout the user: ");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server.error");
		}
	}

	public void changePassword(String newPassword) {
		UserAccount account = findByUserName(Utils.getPrincipal().getUsername());
		String hashPass = passwordEncoder.encode(newPassword);
		account.setPassword(hashPass);
		save(account);
	}

	public JwtDto refreshToken(String oldJwt) {
		try {
			JwtDto jwt = jwtProvider.refreshToken(oldJwt);
			log.info("JWT refreshed...");
			return jwt;
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user.access.denied");
		}
	}

	public void banUser(String bannedUid) {
		UserAccount bannedAccount = accountRepository.findByUidAccount(bannedUid).get();
		log.debug("banUser() - Banning user account: " + bannedAccount.getUsername());
		bannedAccount.setEnabled(false);
		save(bannedAccount);
	}

	public void enableUser(String bannedUid) {
		UserAccount bannedAccount = accountRepository.findByUidAccount(bannedUid).get();
		log.debug("enableUser() - Enabling user account: " + bannedAccount.getUsername());
		bannedAccount.setEnabled(true);
		save(bannedAccount);
	}

}

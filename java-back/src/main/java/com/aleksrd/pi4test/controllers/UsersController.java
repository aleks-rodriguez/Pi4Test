package com.aleksrd.pi4test.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aleksrd.pi4test.dto.admin.statistics.AppStatisticsResponseDto;
import com.aleksrd.pi4test.dto.admin.usersManagement.AllUsersDbResponseDto;
import com.aleksrd.pi4test.dto.admin.usersManagement.BanUnbanRequestDto;
import com.aleksrd.pi4test.dto.admin.usersManagement.ConvertToSuperAdminRequestDto;
import com.aleksrd.pi4test.dto.myData.MyDataResponseDto;
import com.aleksrd.pi4test.services.UserService;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping(value = "/api/users")
@CommonsLog
public class UsersController {
	
	@Autowired
	private UserService userService;

	@GetMapping(value = "/info")
	public ResponseEntity<MyDataResponseDto> getMyData() {
		log.info("GET /api/users/info");
		return ResponseEntity.ok(userService.getAllMyData());
	}
	
	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteAccount() {
		log.info("DELETE /api/users/delete");
		userService.deleteAccount();
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value= "/statistics")
	public ResponseEntity<AppStatisticsResponseDto> getStatistics(){
		log.info("GET /api/users/statistics");
		return new ResponseEntity<AppStatisticsResponseDto>(userService.getUserStats(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = "/userManagement")
	public ResponseEntity<List<AllUsersDbResponseDto>> getAllUsersForManagement() {
		log.info("GET /api/users/userManagement");
		return new ResponseEntity<List<AllUsersDbResponseDto>>(userService.getAllUsersFromSystem(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = "/adminManagement")
	public ResponseEntity<List<AllUsersDbResponseDto>> getAllOtherAdminsForManagement() {
		log.info("GET /api/users/adminManagement");
		return new ResponseEntity<List<AllUsersDbResponseDto>>(userService.getOtherAdminsFromSystem(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/ban")
	public ResponseEntity<Void> banUser(@RequestBody BanUnbanRequestDto dto){
		log.info("POST /api/users/ban");
		userService.banUser(dto.bannedUid());
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/enable")
	public ResponseEntity<Void> enableUser(@RequestBody BanUnbanRequestDto dto){
		log.info("POST /api/users/enable");
		userService.enableUser(dto.bannedUid());
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/convertSuperAdmin")
	public ResponseEntity<Void> convertToSuperAdmin(@RequestBody ConvertToSuperAdminRequestDto uidAccountNewSuperAdmin) {
		log.info("POST /api/users/convertToSuperAdmin");
		userService.convertToSuperAdmin(uidAccountNewSuperAdmin.newSuperAdminUidAccount());
		return ResponseEntity.ok().build();
	}
	

}

package com.aleksrd.pi4test.dto.admin.usersManagement;

import lombok.Getter;

@Getter
public class AllUsersDbResponseDto {
	private String uidAccount;
	private String userName;
	private boolean enabled;
	private boolean superAdmin;
	
	public AllUsersDbResponseDto(String uidAccount, String userName, boolean enabled, boolean superAdmin) {
		this.uidAccount = uidAccount;
		this.userName = userName;
		this.enabled = enabled;
		this.superAdmin = superAdmin;
		
	}

}

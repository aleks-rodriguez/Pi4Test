
package com.aleksrd.pi4test.security.entities;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.aleksrd.pi4test.entities.DomainEntity;
import com.aleksrd.pi4test.security.enums.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Access(AccessType.FIELD)
public class UserAccount extends DomainEntity implements UserDetails {

	private static final long	serialVersionUID	= -6588041930226353211L;

	@NotBlank
	@Column(unique = true)
	private String				userName;

	@NotBlank
	private String				password;

	private LocalDateTime		createdAt;

	@Enumerated(EnumType.STRING)
	private Role				role;

	private boolean				superAdmin;

	private boolean				enabled;

	@NotBlank
	private String				uidAccount;


	public UserAccount(String userName, String password, Role role) {
		this.userName = userName;
		this.password = password;
		this.createdAt = LocalDateTime.now();
		this.role = role;
		this.enabled = true;
		this.uidAccount = UUID.randomUUID().toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.toString());
		return Arrays.asList(authority);
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}

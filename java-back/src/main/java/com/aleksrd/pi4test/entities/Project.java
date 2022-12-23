
package com.aleksrd.pi4test.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.aleksrd.pi4test.security.entities.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Access(AccessType.FIELD)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Project extends DomainEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@ManyToOne(optional = false)
	private UserAccount			account;

	@NotBlank
	private String				title;

	private String				description;

	//Random identifier for the React app
	@NotBlank
	private String				uidProject;

	@NotEmpty
	@JsonIgnore
	@NotBlank
	private String				systemPath;

	@JsonIgnore
	private String				databaseName;

	private boolean				unitTestExecuted;

	private boolean				performanceTestExecuted;

}

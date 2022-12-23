
package com.aleksrd.pi4test.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
public class UserStatistics extends DomainEntity {

	private static final long	serialVersionUID		= -4512375272608404354L;

	@NotEmpty
	@ElementCollection
	private List<String>		totalRegisteredUsers	= new ArrayList<>();

	@ElementCollection
	private List<String>		totalDeletedUsers		= new ArrayList<>();


	public int getActualRegisteredUsers() {
		return totalRegisteredUsers.size() - totalDeletedUsers.size();
	}

}

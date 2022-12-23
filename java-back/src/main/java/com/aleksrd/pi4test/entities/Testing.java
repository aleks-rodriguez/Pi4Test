
package com.aleksrd.pi4test.entities;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.aleksrd.pi4test.enums.TestType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Testing extends DomainEntity {

	static final long		serialVersionUID	= -5848822374676338899L;

	@ManyToOne
	//	@JsonIgnore
	private Project			project;

	private String			uidTest;

	@Enumerated(EnumType.STRING)
	private TestType		type;

	private String			elapsedTime;

	private LocalDateTime	executedAt;

}

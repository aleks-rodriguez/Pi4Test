
package com.aleksrd.pi4test.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SecurityMessage extends DomainEntity {

	private static final long	serialVersionUID	= -1314148887018318493L;

	private String				title;
	private String				uidMessage;
	private String				spMessage;
	private String				enMessage;
}

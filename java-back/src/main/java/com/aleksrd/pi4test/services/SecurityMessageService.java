
package com.aleksrd.pi4test.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleksrd.pi4test.dto.admin.NewSecurityMessageRequestDto;
import com.aleksrd.pi4test.entities.SecurityMessage;
import com.aleksrd.pi4test.repositories.SecurityMessageRepository;
import com.aleksrd.pi4test.utils.Utils;

import lombok.extern.apachecommons.CommonsLog;

@Service
@Transactional
@CommonsLog
public class SecurityMessageService {

	@Autowired
	private SecurityMessageRepository messageRepo;


	public List<SecurityMessage> existAnyMessage() {
		if (messageRepo.existMessage()) {
			return messageRepo.findAll();
		} else {
			return Collections.emptyList();
		}
	}

	public SecurityMessage save(NewSecurityMessageRequestDto dto) {
		SecurityMessage message = new SecurityMessage();
		message.setTitle(dto.title());
		message.setEnMessage(dto.enMessage());
		message.setSpMessage(dto.spMessage());
		message.setUidMessage(UUID.randomUUID().toString());

		SecurityMessage newMessage = messageRepo.save(message);
		log.info("save() - The user: " + Utils.getPrincipal().getUsername() + " is saving a new security message: " + message.getTitle() + ", uid= " + message.getUidMessage());
		return newMessage;
	}

	public void delete(String messageUid) {
		log.info("delete() - The user: " + Utils.getPrincipal().getUsername() + " is deleting the security message with uid: " + messageUid);
		messageRepo.deleteByUidMessage(messageUid);

	}
}

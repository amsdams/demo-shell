package com.example.demo.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.model.CliUser;
import com.example.demo.observer.ProgressUpdateEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Mock implementation of UserService.
 */
@Slf4j
public class MockUserService /* extends Observable */ implements UserService {

	@Autowired
	private ObjectMapper objectMapper;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private List<CliUser> users = new ArrayList<>();

	@Override
	public CliUser findById(Long id) {
		for (CliUser user : users) {
			if (id.equals(user.getId())) {
				return user;
			}
		}
		return null;
	}

	@Override
	public CliUser findByUsername(String username) {
		for (CliUser user : users) {
			if (username.equals(user.getUsername())) {
				return user;
			}
		}
		return null;
	}

	@Override
	public List<CliUser> findAll() {
		return users;
	}

	@Override
	public boolean exists(String username) {
		for (CliUser user : users) {
			if (username.equals(user.getUsername())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public CliUser create(CliUser user) {
		user.setId(getNextId());
		users.add(user);
		return user;
	}

	@Override
	public CliUser update(CliUser user) {
		for (CliUser u : users) {
			if (u.getId().equals(user.getId())) {
				// u = user;
				return user;
			}
		}
		throw new IllegalArgumentException("No matching user found!");
	}

	@Override
	public long updateAll() {
		long numberOfUsers = 2000;
		for (long i = 1; i <= numberOfUsers; i++) {
			// do some operation ...
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.error("caught ex {}", e);
			}

			String message = "";
			if (i < numberOfUsers) {
				message = ":: please WAIT update operation in progress";
			}

			pcs.firePropertyChange("theProperty", new ProgressUpdateEvent(i, numberOfUsers, message),
					new ProgressUpdateEvent(i, numberOfUsers, message));

		}
		return numberOfUsers;
	}

	// --- set / get methods ---------------------------------------------------

	public void addObserver(PropertyChangeListener l) {
		pcs.addPropertyChangeListener("theProperty", l);
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	// --- util methods --------------------------------------------------------

	public void init(String filePath) throws IOException {
		ClassPathResource cpr = new ClassPathResource("cli-users.json");
		users = objectMapper.readValue(cpr.getInputStream(), new TypeReference<List<CliUser>>() {
		});
	}

	private long getNextId() {
		long maxId = 0;
		for (CliUser user : users) {
			if (user.getId().longValue() > maxId) {
				maxId = user.getId().longValue();
			}
		}
		return maxId + 1;
	}
}

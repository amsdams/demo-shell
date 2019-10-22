package com.example.demo.service;

import java.util.List;

import com.example.demo.model.CliUser;

/**
 * Interface describing (Cli) UserService.
 *
 */
public interface UserService {
	CliUser findById(Long id);

	CliUser findByUsername(String username);

	List<CliUser> findAll();

	boolean exists(String username);

	CliUser create(CliUser user);

	CliUser update(CliUser user);

	long updateAll();
}

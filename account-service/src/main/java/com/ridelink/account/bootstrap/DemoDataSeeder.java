package com.ridelink.account.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ridelink.account.domain.AccountStatus;
import com.ridelink.account.domain.Role;
import com.ridelink.account.domain.User;
import com.ridelink.account.repo.UserRepository;

@Component
public class DemoDataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DemoDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		seed("admin@ridelink.local", "Admin123!", "RideLink Admin", Role.ADMIN);
		seed("passenger@ridelink.local", "Pass123!", "Asha Passenger", Role.PASSENGER);
		seed("driver@ridelink.local", "Drive123!", "Nimal Driver", Role.DRIVER);
	}

	private void seed(String email, String password, String name, Role role) {
		if (userRepository.existsByEmailIgnoreCase(email)) {
			return;
		}
		User user = new User();
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(password));
		user.setFullName(name);
		user.setPhone("0770000000");
		user.setRole(role);
		user.setStatus(AccountStatus.ACTIVE);
		userRepository.save(user);
	}
}

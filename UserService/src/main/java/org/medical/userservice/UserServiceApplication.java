package org.medical.userservice;

import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.medical.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepository) {
		return args -> {
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			userRepository.save(new UserEntity(
					"admin@admin.com",
					passwordEncoder.encode("admin"),  // Encode the password
					"admin",
					"admin",
					"089765765",
					"casa",
					RoleEnum.ADMIN)
			);
		};
	}
}

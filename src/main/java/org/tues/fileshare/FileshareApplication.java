package org.tues.fileshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"org.tues.fileshare.Repository"})
@EntityScan(basePackages = {"org.tues.fileshare.Entity"} )
@ComponentScan(basePackages = {"org.tues.fileshare.Service"})
@ComponentScan(basePackages = {"org.tues.fileshare.Controller"})

public class FileshareApplication {
	public static void main(String[] args) {
		SpringApplication.run(FileshareApplication.class, args);
	}
}

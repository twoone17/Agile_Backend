package com.f3f.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Application {
	// 김동준 테스트 커밋
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

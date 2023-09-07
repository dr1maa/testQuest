package com.tq.testQuest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestQuestApplication {

	public static void main(String[] args) {

		SpringApplication.run(TestQuestApplication.class, args);
	}

}

package com.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.main"})
@MapperScan(basePackages = {"com.main.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class ApiHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiHubApplication.class, args);
	}

}

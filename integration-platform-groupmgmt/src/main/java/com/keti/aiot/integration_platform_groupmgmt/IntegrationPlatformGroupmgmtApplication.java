package com.keti.aiot.integration_platform_groupmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.keti.aiot.integration_platform_groupmgmt")
public class IntegrationPlatformGroupmgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationPlatformGroupmgmtApplication.class, args);
	}

}

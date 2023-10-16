package com.camerinfolks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EntityScan("com.camerinfolks.model")
@EntityScan(basePackages = {"com.camerinfolks.*"})
@ComponentScan({"com.camerinfolks.*"})
public class ZippyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZippyApplication.class, args);
	}

}


package com.aleksrd.pi4test;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.aleksrd.pi4test")
public class Pi4TestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws IOException {
		 SpringApplication.run(Pi4TestApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Pi4TestApplication.class);
	}

}

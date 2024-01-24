package com.ls.tc.speech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class CommonConfig {

	public CommonConfig() {
		// noop
	}

	// for parsing LocalDate
	@Bean
	Module javaTimeModule() {
		return new JavaTimeModule();
	}
}

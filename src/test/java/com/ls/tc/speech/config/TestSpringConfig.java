package com.ls.tc.speech.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ls.tc.speech.controller.SpeechController;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { SpeechController.class })
public class TestSpringConfig {

	public TestSpringConfig() {
		// noop
	}
}

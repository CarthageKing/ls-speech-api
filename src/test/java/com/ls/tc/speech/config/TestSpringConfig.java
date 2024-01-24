package com.ls.tc.speech.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ls.tc.speech.controller.SpeechController;
import com.ls.tc.speech.dao.SpeechSearchEntityDao;
import com.ls.tc.speech.exception.SpeechAppExceptionHandler;
import com.ls.tc.speech.service.SpeechService;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { SpeechController.class, SpeechAppExceptionHandler.class, SpeechService.class, SpeechSearchEntityDao.class })
public class TestSpringConfig {

	public TestSpringConfig() {
		// noop
	}
}

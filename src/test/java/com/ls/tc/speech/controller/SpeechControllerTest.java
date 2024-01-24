package com.ls.tc.speech.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import com.ls.tc.speech.config.CommonConfig;
import com.ls.tc.speech.config.TestSpringConfig;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SpeechControllerTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SpeechControllerTest.class);

	@LocalServerPort
	private int port;

	private String baseUrl;

	private RestTemplate restTemplate = new RestTemplate();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		baseUrl = "http://localhost:" + port;
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_CRUD() throws Exception {
		{
			String requestUriStr = baseUrl + "/speeches/o/1";
			String content = null;
			HttpHeaders hdrs = new HttpHeaders();
			hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);

			ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.GET, httpReq, String.class);

			LOG.trace("the response: {}", httpRsp.getBody());
			Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		}
	}
}

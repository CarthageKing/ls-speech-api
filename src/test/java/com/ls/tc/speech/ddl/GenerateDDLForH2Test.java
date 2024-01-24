package com.ls.tc.speech.ddl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.ls.tc.speech.config.CommonConfig;
import com.ls.tc.speech.config.TestDbConfig;
import com.ls.tc.speech.config.TestSpringConfig;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
	// below properties don't need to be set since our default embedded
	// db implementation used is H2. But if the default is different and we
	// want to generate the ddl for H2, then the below properties need to
	// be set
	//"spring.datasource.url=jdbc:h2:mem:testdb",
	//"spring.datasource.driver-class-name=org.h2.Driver",
	//"spring.datasource.username=sa",
	//"spring.datasource.password=",

	// https://thorben-janssen.com/standardized-schema-generation-data-loading-jpa-2-1/
	//
	// Options used to tell Spring and Hibernate on how to perform the
	// DDL generation. Instead of using javax, use jakarta. However, if using
	// older than Java 17 or using libraries that have not been ported to
	// jakarta, then just use the javax package. The options below will create
	// a create.sql file in the target folder
	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create",
	"spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/create.sql",
	// below is to ensure the generated sql is pretty-printed at least
	"spring.jpa.properties.hibernate.format_sql=true",
})
class GenerateDDLForH2Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_generateDdl() {
		// do nothing. add the below to get rid of warning saying that this
		// test method does not have any assertions
		Assertions.assertTrue(true);
	}
}

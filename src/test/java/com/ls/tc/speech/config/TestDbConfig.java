package com.ls.tc.speech.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ls.tc.speech.dao.SpeechEntityDao;
import com.ls.tc.speech.dao.entity.SpeechEntity;
import com.ls.tc.speech.service.DbHelper;

@Configuration
@EnableJpaRepositories(basePackageClasses = { SpeechEntityDao.class })
@EntityScan(basePackageClasses = { SpeechEntity.class })
@EnableTransactionManagement
public class TestDbConfig {

	public TestDbConfig() {
		// noop
	}

	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			// we want our tables etc. to be created in a specific schema, not
			// 'public' or the default schema. This is to facilitate separation
			// of concerns. So, when the embedded database is being built, we
			// also execute a script which will contain the commands to create
			// the schema we desire
			.addScript("ddl/00_create_schema.sql")
			// setting below prevents reuse of databases and data from previous
			// tests
			.generateUniqueName(true)
			.build();
	}

	@Bean
	DbHelper dbHelper() {
		return new DbHelper();
	}
}

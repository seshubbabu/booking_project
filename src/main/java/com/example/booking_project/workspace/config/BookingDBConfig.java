package com.example.booking_project.workspace.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Sivasankar.Thalavai
 *
 *         Apr 3, 2023
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.booking_project.workspace", entityManagerFactoryRef = "bookingEntityManagerFactory", transactionManagerRef = "bookingTransactionManager")
public class BookingDBConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.booking")
	public DataSourceProperties bookingDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.booking.configuration")
	public DataSource bookingDataSource() {
		return bookingDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "bookingEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean bookingEntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(bookingDataSource()).packages("com.example.booking_project.workspace").build();
	}

	@Bean
	@Primary
	public PlatformTransactionManager bookingTransactionManager(
			final @Qualifier("bookingEntityManagerFactory") LocalContainerEntityManagerFactoryBean bookingEntityManagerFactory) {
		return new JpaTransactionManager(bookingEntityManagerFactory.getObject());
	}
}

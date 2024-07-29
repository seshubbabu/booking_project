/**
 * 
 */
package com.example.booking_project.workspace.config;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@Configuration
public class WorkspaceBookingConfiguarion {

	Logger logger = LoggerFactory.getLogger(WorkspaceBookingConfiguarion.class);

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.setAllowedMethods(Arrays.asList(CorsConfiguration.ALL));
		corsConfiguration.setAllowedHeaders(Arrays.asList(CorsConfiguration.ALL));
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

}
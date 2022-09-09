package com.formedix.exercise.configs;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

import com.formedix.exercise.repositories.RateRepository;
import com.formedix.exercise.services.RateService;

@Configuration
public class TestConfiguration {
	
	@MockBean
	private RateService rateService;
	
	@MockBean
	private RateRepository rateRepository; 
}

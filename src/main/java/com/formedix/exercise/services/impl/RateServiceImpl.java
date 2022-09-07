package com.formedix.exercise.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.formedix.exercise.services.RateService;

@Service
public class RateServiceImpl implements RateService {

	private Map<String, Map<String, Double>> rates = new HashMap<>();
	
	public RateServiceImpl() {}
	
	
}

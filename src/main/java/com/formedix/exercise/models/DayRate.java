package com.formedix.exercise.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.formedix.exercise.exceptions.RateNotFoundException;

public class DayRate {
	
	private Map<String, Double> rates;
	
	public DayRate(){
		rates = new HashMap<>();
	}
	
	public void setCurrencyRate(String currency, Double value) {
		rates.put(currency, value);
	}
	
	public Map<String, Double> getRates() {
		return rates;
	}
	
	public Double getCurrencyRate(String currency) {		
		return Optional.ofNullable(this.rates.get(currency))
				.orElseThrow(() -> new RateNotFoundException(currency));
	}
}

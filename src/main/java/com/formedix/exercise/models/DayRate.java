package com.formedix.exercise.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.formedix.exercise.exceptions.NotFoundException;

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
				.orElseThrow(() -> new NotFoundException("Currency " + currency + " not found"));
	}

	@Override
	public int hashCode() {
		return Objects.hash(rates);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DayRate other = (DayRate) obj;
		return Objects.equals(rates, other.rates);
	}
}

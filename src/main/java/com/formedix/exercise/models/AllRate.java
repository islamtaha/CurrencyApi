package com.formedix.exercise.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.formedix.exercise.exceptions.DateNotFoundException;

public class AllRate {

	Map<Date, DayRate> allRates;
	
	public AllRate() {
		allRates = new HashMap<>();
	}
	
	public void setRateInDate(Date date, String currency, Double rate) {
		this.allRates.computeIfAbsent(date, key -> new DayRate()).setCurrencyRate(currency, rate);
	}
	
	public double getRateInDate(Date date, String currency) {
		return Optional.ofNullable(this.allRates.get(date))
				.orElseThrow(() -> new DateNotFoundException(date))
				.getCurrencyRate(currency);
	}
	
	public Map<String, Double> getAllCurrencyRatesInDate(Date date) {
		return Optional.ofNullable(this.allRates.get(date))
				.orElseThrow(() -> new DateNotFoundException(date))
				.getRates();
	}
	
	public Map<Date, DayRate> getAllRates() {
		return allRates;
	}
}

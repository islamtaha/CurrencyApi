package com.formedix.exercise.repositories;

import java.util.Date;
import java.util.Map;

import com.formedix.exercise.models.DayRate;

public interface RateRepository {
	
	public Double getRateInDate(Date date, String currency);
	
	public Map<String, Double> getAllCurrencyRatesInDate(Date date);
	
	public Map<Date, DayRate> getAllRates();
}

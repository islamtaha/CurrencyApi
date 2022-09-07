package com.formedix.exercise.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateRate {

	Map<Date, DayRate> dateRates;
	
	public DateRate() {
		dateRates = new HashMap<>();
	}
	
	public void setCurrencyExchangeRate (Date date, String currency, Double rate) {
		this.dateRates.computeIfAbsent(date, key -> {
			DayRate dayRate = new DayRate();
			dayRate.setCurrencyRate(currency, rate);
			return dayRate;
		});
	}
	
}

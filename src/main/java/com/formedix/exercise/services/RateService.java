package com.formedix.exercise.services;

import java.util.Date;
import java.util.Map;

public interface RateService {

	public Map<String, Double> getAllCurrencyRatesInDate(Date date);
	
	public Double convertAmount(String sourceCurrency, String targetCurrency, Double amount, Date date);
	
	public Double maxRateInRange(String currency, Date start, Date finish);
	
	public Double averageRateInRange(String currency, Date start, Date finish);
}

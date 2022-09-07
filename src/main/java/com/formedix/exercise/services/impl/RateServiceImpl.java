package com.formedix.exercise.services.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formedix.exercise.exceptions.IncorrectDateRangeException;
import com.formedix.exercise.repositories.RateRepository;
import com.formedix.exercise.services.RateService;

@Service
public class RateServiceImpl implements RateService {

	@Autowired
	private RateRepository rateRepository;
		
	public Map<String, Double> getAllCurrencyRatesInDate(Date date) {
		return rateRepository.getAllCurrencyRatesInDate(date);
	}
	
	public Double convertAmount(String sourceCurrency, String targetCurrency, Double amount, Date date) {
		Double sourceRate = rateRepository.getRateInDate(date, sourceCurrency);
		Double targetRate = rateRepository.getRateInDate(date, targetCurrency);
		return amount / sourceRate * targetRate;
	}
	
	// returns NaN if there is no rates found in the given range
	public Double maxRateInRange(String currency, Date start, Date finish) {
		
		if (start.after(finish)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			throw new IncorrectDateRangeException(df.format(start), df.format(finish));
		}
		
		return rateRepository.getAllRates().entrySet().stream()
			.filter(entry -> isBetweenDates(start, finish, entry.getKey()))
			.filter(entry -> entry.getValue().getRates().containsKey(currency))
			.mapToDouble(entry -> entry.getValue().getCurrencyRate(currency))
			.max()
			.orElse(Double.NaN);
	}
	
	public Double averageRateInRange(String currency, Date start, Date finish) {
		
		if (start.after(finish)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			throw new IncorrectDateRangeException(df.format(start), df.format(finish));
		}
		
		return rateRepository.getAllRates().entrySet().stream()
				.filter(entry -> isBetweenDates(start, finish, entry.getKey()))
				.filter(entry -> entry.getValue().getRates().containsKey(currency))
				.mapToDouble(entry -> entry.getValue().getCurrencyRate(currency))
				.average()
				.orElse(Double.NaN);
	}
	
	private boolean isBetweenDates(Date start, Date finish, Date current) {
		return start.compareTo(current) <= 0 && finish.compareTo(current) >= 0;
	}
}

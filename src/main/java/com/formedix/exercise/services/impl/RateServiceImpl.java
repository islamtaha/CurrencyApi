package com.formedix.exercise.services.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formedix.exercise.exceptions.BadRequestException;
import com.formedix.exercise.exceptions.NotFoundException;
import com.formedix.exercise.repositories.RateRepository;
import com.formedix.exercise.services.RateService;

@Service
public class RateServiceImpl implements RateService {
	
    private Logger logger = LoggerFactory.getLogger(RateServiceImpl.class);

	@Autowired
	private RateRepository rateRepository;
	
	RateServiceImpl() {}
	
	// for testing
	RateServiceImpl(RateRepository rateRepository) {
		this.rateRepository = rateRepository;
	}
		
	public Map<String, Double> getAllCurrencyRatesInDate(Date date) {
		return rateRepository.getAllCurrencyRatesInDate(date);
	}
	
	public Double convertAmount(String sourceCurrency, String targetCurrency, Double amount, Date date) {
		
		Double sourceRate = rateRepository.getRateInDate(date, sourceCurrency);
		logger.info("Source currency: {}, source rate: {}", sourceCurrency, sourceRate);
		
		Double targetRate = rateRepository.getRateInDate(date, targetCurrency);
		logger.info("Target currency: {}, target rate: {}", sourceCurrency, sourceRate);
		
		return amount / sourceRate * targetRate;
	}
	
	public Double maxRateInRange(String currency, Date start, Date finish) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (start.after(finish)) {
			throw new BadRequestException(String.format("date range [%s - %s] is incorrect - start must be before finish",
					df.format(start), df.format(finish)));
		}
		
		return rateRepository.getAllRates().entrySet().stream()
			.filter(entry -> isBetweenDates(start, finish, entry.getKey()))
			.filter(entry -> entry.getValue().getRates().containsKey(currency))
			.mapToDouble(entry -> entry.getValue().getCurrencyRate(currency))
			.max()
			.orElseThrow(() -> {
				String message = String.format("Result not found for input[currency: %s, start: %s, finsih: %s]", 
						currency, df.format(start), df.format(finish));
				return new NotFoundException(message);
			});
	}
	
	public Double averageRateInRange(String currency, Date start, Date finish) {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (start.after(finish)) {
			throw new BadRequestException(String.format("date range [%s - %s] is incorrect - start must be before finish",
					df.format(start), df.format(finish)));		}
		
		return rateRepository.getAllRates().entrySet().stream()
			.filter(entry -> isBetweenDates(start, finish, entry.getKey()))
			.filter(entry -> entry.getValue().getRates().containsKey(currency))
			.mapToDouble(entry -> entry.getValue().getCurrencyRate(currency))
			.average()
			.orElseThrow(() -> {
				String message = String.format("Result not found for input[currency: %s, start: %s, finsih: %s]", 
						currency, df.format(start), df.format(finish));
				return new NotFoundException(message);
			});
	}
	
	private boolean isBetweenDates(Date start, Date finish, Date current) {
		return start.compareTo(current) <= 0 && finish.compareTo(current) >= 0;
	}
}

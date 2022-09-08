package com.formedix.exercise.controllers;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formedix.exercise.services.RateService;

@RestController
@RequestMapping(path = "api/v1/rates")
public class CurrencyController {

	@Autowired
	private RateService rateService;
	
	@GetMapping()
	public Map<String, Double> getByDate(
			@RequestParam(value = "date", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date date){
		
		return rateService.getAllCurrencyRatesInDate(date);
	}
	
	@GetMapping("/convert")
	public Double convertAmount(
			@RequestParam(value = "source", required = true)
			String source,
			
			@RequestParam(value = "target", required = true)
			String target,
			
			@RequestParam(value = "amount", required = true)
			Double amount,
			
			@RequestParam(value = "date", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date date
			){
		
		return rateService.convertAmount(source, target, amount, date);
	}

	@GetMapping("/max")
	public Double getMaxInRange(
			@RequestParam(value = "start", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date start,
			
			@RequestParam(value = "finish", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date finish,
			
			@RequestParam(value = "currency", required = true)
			String currency
			){
		
		return rateService.maxRateInRange(currency, start, finish);
	}
	
	@GetMapping("/average")
	public Double getAverageInRange(
			@RequestParam(value = "start", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date start,
			
			@RequestParam(value = "finish", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date finish,
			
			@RequestParam(value = "currency", required = true)
			String currency
			){
		
		return rateService.averageRateInRange(currency, start, finish);
	}
}

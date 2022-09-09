package com.formedix.exercise.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.formedix.exercise.services.RateService;

@Validated
@RestController
@RequestMapping(path = "api/v1/rates")
public class CurrencyController {

	private static final String RESULT_KEY = "result";
	
	@Autowired
	private RateService rateService;
	
	@GetMapping()
	public ResponseEntity<Map<String, Double>> getByDate(
			@RequestParam(value = "date", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date date){
		
		return ResponseEntity.ok(rateService.getAllCurrencyRatesInDate(date));
	}
	
	@GetMapping("/convert")
	public ResponseEntity<Map<String, Double>> convert(
			@RequestParam(value = "source", required = true)
			@Size(min=3, max=3)
			String source,
			
			@RequestParam(value = "target", required = true)
			@Size(min=3, max=3)
			String target,
			
			@RequestParam(value = "amount", required = true)
			Double amount,
			
			@RequestParam(value = "date", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date date
			){
		
		Map<String, Double> response = new HashMap<>();
	    response.put(RESULT_KEY, rateService.convertAmount(source, target, amount, date));
		return ResponseEntity.ok(response);
	}

	@GetMapping("/max")
	public ResponseEntity<Map<String, Double>> max(
			@RequestParam(value = "start", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date start,
			
			@RequestParam(value = "finish", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date finish,
			
			@RequestParam(value = "currency", required = true)
			@Size(min=3, max=3)
			String currency
			){
		
	    Map<String, Double> response = new HashMap<>();
	    response.put(RESULT_KEY, rateService.maxRateInRange(currency, start, finish));
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/average")
	public ResponseEntity<Map<String, Double>> average(
			@RequestParam(value = "start", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date start,
			
			@RequestParam(value = "finish", required = true)
			@DateTimeFormat(pattern="yyyy-MM-dd")
			Date finish,
			
			@RequestParam(value = "currency", required = true)
			@Size(min=3, max=3)
			String currency
			){
		
		Map<String, Double> response = new HashMap<>();
		response.put(RESULT_KEY, rateService.averageRateInRange(currency, start, finish));
		return ResponseEntity.ok(response);
	}
}

package com.formedix.exercise.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formedix.exercise.models.Currency;
import com.formedix.exercise.services.RateService;

@RestController
@RequestMapping(path = "api/v1/rates")
public class CurrencyController {

	@Autowired
	private RateService rateService;
	
	@GetMapping("date")
	List<Currency> getByDate(){
		ArrayList<Currency> arr = new ArrayList<>();
		arr.add(new Currency("", 0.0));
		return arr;
	}

}

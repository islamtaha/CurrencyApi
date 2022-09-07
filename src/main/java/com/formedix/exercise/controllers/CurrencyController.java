package com.formedix.exercise.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.formedix.exercise.entities.Currency;

@RestController
public class CurrencyController {

	@GetMapping("/date")
	List<Currency> getByDate(){
		ArrayList<Currency> arr = new ArrayList<>();
		arr.add(new Currency("", 0.0));
		return arr;
	}

}
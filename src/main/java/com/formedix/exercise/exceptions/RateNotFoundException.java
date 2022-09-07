package com.formedix.exercise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RateNotFoundException extends ResponseStatusException {
		
	private final String currency;
	
	public RateNotFoundException(String currency) {
		super(HttpStatus.BAD_REQUEST, "Requested Currency " + currency + " not found");
		this.currency = currency;
	}
	
	@Override
	public String toString() {
		return "No Rate found for Currency: " + currency;
	}

}

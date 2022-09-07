package com.formedix.exercise.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DateNotFoundException extends ResponseStatusException {

	private final Date date;
	
	public DateNotFoundException(Date date) {
		super(HttpStatus.BAD_REQUEST, "Requested date " + date + " is found");
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "No Rate found for date: " + date;
	}
}

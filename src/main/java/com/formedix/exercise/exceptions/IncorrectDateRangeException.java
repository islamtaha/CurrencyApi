package com.formedix.exercise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectDateRangeException extends ResponseStatusException {
	public IncorrectDateRangeException(String start, String finish) {
		super(HttpStatus.BAD_REQUEST, "Requested date range [ " + start + " - " + finish + " ] is incorrect - start must be before finish");
	}
}

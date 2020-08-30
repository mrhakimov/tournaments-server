package com.hakimov.tournament.exception;

import org.springframework.http.HttpStatus;

public class TournamentExceptionResponse {
	private final String message;
	private final HttpStatus status;

	public TournamentExceptionResponse(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}
}

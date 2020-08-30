package com.hakimov.tournament.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TournamentExceptionHandler {
	@ExceptionHandler(TournamentException.class)
	public ResponseEntity<Object> handleTournamentException(TournamentException e) {
		return new ResponseEntity<>(new TournamentExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST),
			HttpStatus.BAD_REQUEST);
	}
}

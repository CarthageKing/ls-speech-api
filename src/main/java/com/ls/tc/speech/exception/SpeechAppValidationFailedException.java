package com.ls.tc.speech.exception;

import java.util.Set;

import jakarta.validation.ConstraintViolation;

public class SpeechAppValidationFailedException extends SpeechAppException {

	private static final long serialVersionUID = 8485809729088437515L;

	private final Set<ConstraintViolation<?>> errors;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SpeechAppValidationFailedException(Set errors) {
		super("Validation failure");
		this.errors = errors;
	}

	public Set<ConstraintViolation<?>> getErrors() {
		return errors;
	}
}

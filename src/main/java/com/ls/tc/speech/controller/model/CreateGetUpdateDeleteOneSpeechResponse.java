package com.ls.tc.speech.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class CreateGetUpdateDeleteOneSpeechResponse extends GenericResponse<Speech> {

	private static final long serialVersionUID = 3018611379368230916L;

	public CreateGetUpdateDeleteOneSpeechResponse() {
		// noop
	}
}

package com.ls.tc.speech.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ErrorListResponse extends GenericResponse<ErrorList> {

	private static final long serialVersionUID = 3018611379368230916L;

	public ErrorListResponse() {
		// noop
	}
}

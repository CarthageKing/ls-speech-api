package com.ls.tc.speech.controller.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ErrorList implements java.io.Serializable {

	private static final long serialVersionUID = 1544842777321113L;

	private List<String> errorMessages = new ArrayList<>();

	public ErrorList() {
		// noop
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
}

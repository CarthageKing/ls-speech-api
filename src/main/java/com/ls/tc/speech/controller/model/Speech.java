package com.ls.tc.speech.controller.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Speech implements java.io.Serializable {

	private static final long serialVersionUID = -8806385942603963384L;

	private String id;

	// the annotation is to tell Jackson to format this as a string when writing out the JSON
	@JsonFormat(shape = Shape.STRING)
	private LocalDate speechDate;

	private List<String> authors = new ArrayList<>();
	private List<String> keywords = new ArrayList<>();
	private String speechText;

	public Speech() {
		// noop
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDate getSpeechDate() {
		return speechDate;
	}

	public void setSpeechDate(LocalDate speechDate) {
		this.speechDate = speechDate;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getSpeechText() {
		return speechText;
	}

	public void setSpeechText(String speechText) {
		this.speechText = speechText;
	}
}

package com.ls.tc.speech.controller.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ls.tc.speech.util.SpeechAppConstants;
import com.ls.tc.speech.validation.CreateSpeechValidationGroup;
import com.ls.tc.speech.validation.NonBlankStringListElements;
import com.ls.tc.speech.validation.PartialUpdateSpeechValidationGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(Include.NON_EMPTY)
public class Speech implements java.io.Serializable {

	private static final long serialVersionUID = -8806385942603963384L;

	@Size(max = SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH, groups = { CreateSpeechValidationGroup.class, PartialUpdateSpeechValidationGroup.class })
	private String id;

	@NotNull(groups = { CreateSpeechValidationGroup.class })
	// the annotation is to tell Jackson to format this as a string when writing out the JSON
	@JsonFormat(shape = Shape.STRING)
	private LocalDate speechDate;

	@NotEmpty(groups = { CreateSpeechValidationGroup.class })
	@NonBlankStringListElements(max = SpeechAppConstants.SPEECH_AUTHOR_MAX_LENGTH, groups = { CreateSpeechValidationGroup.class, PartialUpdateSpeechValidationGroup.class })
	private List<String> authors = new ArrayList<>();

	@NotEmpty(groups = { CreateSpeechValidationGroup.class })
	@NonBlankStringListElements(max = SpeechAppConstants.SPEECH_KEYWORD_MAX_LENGTH, groups = { CreateSpeechValidationGroup.class, PartialUpdateSpeechValidationGroup.class })
	private List<String> keywords = new ArrayList<>();

	@NotBlank(groups = { CreateSpeechValidationGroup.class })
	@Size(max = SpeechAppConstants.SPEECH_TEXT_MAX_LENGTH, groups = { CreateSpeechValidationGroup.class, PartialUpdateSpeechValidationGroup.class })
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

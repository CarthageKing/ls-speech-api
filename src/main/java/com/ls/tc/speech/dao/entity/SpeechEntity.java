package com.ls.tc.speech.dao.entity;

import java.sql.Date;

import com.ls.tc.speech.util.SpeechAppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "speech")
public class SpeechEntity implements java.io.Serializable {

	private static final long serialVersionUID = -3249744679692853018L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "s_id", length = SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH)
	private String id;

	@Column(name = "s_speech_date", nullable = false)
	private Date speechDate;

	@Column(name = "s_speech_text", nullable = false, length = Integer.MAX_VALUE)
	private String speechText;

	public SpeechEntity() {
		// noop
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getSpeechDate() {
		return speechDate;
	}

	public void setSpeechDate(Date speechDate) {
		this.speechDate = speechDate;
	}

	public String getSpeechText() {
		return speechText;
	}

	public void setSpeechText(String speechText) {
		this.speechText = speechText;
	}
}

package com.ls.tc.speech.dao.entity;

import java.util.Objects;

import com.ls.tc.speech.util.SpeechAppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
@Entity
@Table(name = "speech_author")
public class SpeechAuthorEntity implements java.io.Serializable {

	private static final long serialVersionUID = 196224394004489123L;

	@EmbeddedId
	private SpeechAuthorityEntityPK id;

	public SpeechAuthorEntity() {
		// noop
	}

	public SpeechAuthorityEntityPK getId() {
		return id;
	}

	public void setId(SpeechAuthorityEntityPK id) {
		this.id = id;
	}

	@Embeddable
	public static class SpeechAuthorityEntityPK implements java.io.Serializable {

		private static final long serialVersionUID = 7677511964980856466L;

		@Column(name = SpeechAppConstants.SPEECH_AUTHOR_ENTITY_SPEECH_ID_FK_COLNAME, length = SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH)
		private String speechId;

		@Column(name = "sa_author", length = SpeechAppConstants.SPEECH_AUTHOR_MAX_LENGTH)
		private String author;

		public SpeechAuthorityEntityPK() {
			// noop
		}

		public SpeechAuthorityEntityPK(String speechId, String author) {
			this.speechId = speechId;
			this.author = author;
		}

		public String getSpeechId() {
			return speechId;
		}

		public String getAuthor() {
			return author;
		}

		@Override
		public int hashCode() {
			return Objects.hash(author, speechId);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof SpeechAuthorityEntityPK)) {
				return false;
			}
			SpeechAuthorityEntityPK other = (SpeechAuthorityEntityPK) obj;
			return Objects.equals(author, other.author) && Objects.equals(speechId, other.speechId);
		}
	}
}

package com.ls.tc.speech.dao.entity;

import java.util.Objects;

import com.ls.tc.speech.util.SpeechAppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "speech_keyword")
public class SpeechKeywordEntity implements java.io.Serializable {

	private static final long serialVersionUID = 196224394004489123L;

	@EmbeddedId
	private SpeechKeywordEntityPK id;

	public SpeechKeywordEntity() {
		// noop
	}

	public SpeechKeywordEntityPK getId() {
		return id;
	}

	public void setId(SpeechKeywordEntityPK id) {
		this.id = id;
	}

	@Embeddable
	public static class SpeechKeywordEntityPK implements java.io.Serializable {

		private static final long serialVersionUID = -4395795851042978780L;

		@Column(name = SpeechAppConstants.SPEECH_AUTHOR_ENTITY_SPEECH_ID_FK_COLNAME, length = SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH)
		private String speechId;

		@Column(name = "sk_keyword", length = SpeechAppConstants.SPEECH_KEYWORD_MAX_LENGTH)
		private String keyword;

		public SpeechKeywordEntityPK() {
			// noop
		}

		public SpeechKeywordEntityPK(String speechId, String keyword) {
			this.speechId = speechId;
			this.keyword = keyword;
		}

		public String getSpeechId() {
			return speechId;
		}

		public String getKeyword() {
			return keyword;
		}

		@Override
		public int hashCode() {
			return Objects.hash(keyword, speechId);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof SpeechKeywordEntityPK)) {
				return false;
			}
			SpeechKeywordEntityPK other = (SpeechKeywordEntityPK) obj;
			return Objects.equals(keyword, other.keyword) && Objects.equals(speechId, other.speechId);
		}
	}
}

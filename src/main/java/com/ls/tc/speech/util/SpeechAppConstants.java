package com.ls.tc.speech.util;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public final class SpeechAppConstants {

	public static final int SPEECH_ENTITY_ID_MAX_LENGTH = 64;
	public static final int SPEECH_AUTHOR_MAX_LENGTH = 1024;
	public static final int SPEECH_KEYWORD_MAX_LENGTH = 1024;

	// just big enough so that Hibernate wouldn't convert the column to a clob/blob/text
	public static final int SPEECH_TEXT_MAX_LENGTH = (1024 * 1024 * 1);

	public static final String SPEECH_AUTHOR_ENTITY_SPEECH_ID_FK_COLNAME = "s_speech_id";

	public static final String SEARCH_PARAM_MULTIVALUE_SEPARATOR = "|";

	public static final DateTimeFormatter STRICT_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

	private SpeechAppConstants() {
		// noop
	}
}

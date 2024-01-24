package com.ls.tc.speech.exception;

public class SpeechAppException extends RuntimeException {

	private static final long serialVersionUID = 8485809729088437515L;

	public SpeechAppException() {
		// noop
	}

	public SpeechAppException(String msg) {
		super(msg);
	}

	public SpeechAppException(Throwable cause) {
		super(cause);
	}

	public SpeechAppException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

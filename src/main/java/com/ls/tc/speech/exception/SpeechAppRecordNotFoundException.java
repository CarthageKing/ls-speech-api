package com.ls.tc.speech.exception;

public class SpeechAppRecordNotFoundException extends SpeechAppException {

	private static final long serialVersionUID = 8485809729088437515L;

	public SpeechAppRecordNotFoundException() {
		// noop
	}

	public SpeechAppRecordNotFoundException(String msg) {
		super(msg);
	}

	public SpeechAppRecordNotFoundException(Throwable cause) {
		super(cause);
	}

	public SpeechAppRecordNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

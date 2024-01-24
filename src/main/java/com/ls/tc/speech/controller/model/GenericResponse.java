package com.ls.tc.speech.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class GenericResponse<T extends java.io.Serializable> implements java.io.Serializable {

	private static final long serialVersionUID = 2198624286618367019L;

	private GenericResponseHeader header;
	private T data;

	public GenericResponse() {
		// noop
	}

	public GenericResponseHeader getHeader() {
		return header;
	}

	public void setHeader(GenericResponseHeader header) {
		this.header = header;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@JsonInclude(Include.NON_EMPTY)
	public static class GenericResponseHeader implements java.io.Serializable {

		private static final long serialVersionUID = 6035493325976244203L;

		private String statusCode;
		private String statusMessage;

		public GenericResponseHeader() {
			// noop
		}

		public String getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(String statusCode) {
			this.statusCode = statusCode;
		}

		public String getStatusMessage() {
			return statusMessage;
		}

		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
	}
}

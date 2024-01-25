package com.ls.tc.speech.exception;

import java.io.Serializable;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ls.tc.speech.controller.model.ErrorList;
import com.ls.tc.speech.controller.model.GenericResponse;
import com.ls.tc.speech.controller.model.GenericResponse.GenericResponseHeader;

import jakarta.validation.ConstraintViolation;

@ControllerAdvice
public class SpeechAppExceptionHandler extends ResponseEntityExceptionHandler {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SpeechAppExceptionHandler.class);

	public SpeechAppExceptionHandler() {
		// noop
	}

	@ExceptionHandler({ SpeechAppException.class })
	public ResponseEntity<GenericResponse<Serializable>> handleAppCustomException(Exception e, WebRequest request) {
		GenericResponse<Serializable> rsp = new GenericResponse<>();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		if (e instanceof SpeechAppRecordNotFoundException) {
			hdr.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
			hdr.setStatusMessage(e.getMessage());
		} else if (e instanceof SpeechAppValidationFailedException) {
			hdr.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
			hdr.setStatusMessage(e.getMessage());

			SpeechAppValidationFailedException ex = (SpeechAppValidationFailedException) e;
			Set<ConstraintViolation<?>> errors = ex.getErrors();
			ErrorList errlst = convertToErrorList(errors);
			rsp.setData(errlst);
		} else {
			// for generic exceptions, treat everything as sensitive info and do
			// not leak to the response object
			hdr.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
			hdr.setStatusMessage("An internal error occurred");
		}
		LOG.error("An error occurred", e);
		return ResponseEntity.status(HttpStatus.valueOf(Integer.valueOf(hdr.getStatusCode()))).body(rsp);
	}

	private ErrorList convertToErrorList(Set<ConstraintViolation<?>> errors) {
		ErrorList errlst = new ErrorList();
		for (ConstraintViolation<?> cv : errors) {
			errlst.getErrorMessages().add("'" + cv.getPropertyPath() + "' " + cv.getMessage());
		}
		return errlst;
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<GenericResponse<Serializable>> handleGenericException(Exception e, WebRequest request) {
		GenericResponse<Serializable> rsp = new GenericResponse<>();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		// for generic exceptions, treat everything as sensitive info and do
		// not leak to the response object
		hdr.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		hdr.setStatusMessage("An internal error occurred");
		LOG.error("An error occurred", e);
		return ResponseEntity.status(HttpStatus.valueOf(Integer.valueOf(hdr.getStatusCode()))).body(rsp);
	}
}

package com.ls.tc.speech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ls.tc.speech.controller.model.GenericResponse;
import com.ls.tc.speech.controller.model.GenericResponse.GenericResponseHeader;

@RequestMapping("/speeches")
@RestController
public class SpeechController {

	public SpeechController() {
		// noop
	}

	@GetMapping(value = "/o/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse<String> getSpeechById(@PathVariable String id) {
		GenericResponse<String> rsp = new GenericResponse<>();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage("Operation successful");
		return rsp;
	}
}

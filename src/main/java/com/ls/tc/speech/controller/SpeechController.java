package com.ls.tc.speech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ls.tc.speech.controller.model.CreateGetUpdateDeleteOneSpeechResponse;
import com.ls.tc.speech.controller.model.GenericResponse.GenericResponseHeader;
import com.ls.tc.speech.controller.model.ListResponseContainer;
import com.ls.tc.speech.controller.model.SearchSpeechResponse;
import com.ls.tc.speech.controller.model.Speech;
import com.ls.tc.speech.service.SpeechService;

import jakarta.annotation.Resource;

@RequestMapping("/speeches")
@RestController
public class SpeechController {

	@Resource
	private SpeechService speechSvc;

	public SpeechController() {
		// noop
	}

	@PostMapping(value = "/o", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public CreateGetUpdateDeleteOneSpeechResponse createSpeech(@RequestBody Speech speech) {
		Speech createdSpeech = speechSvc.createSpeech(speech);
		CreateGetUpdateDeleteOneSpeechResponse rsp = new CreateGetUpdateDeleteOneSpeechResponse();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.CREATED.value()));
		hdr.setStatusMessage("Speech data created");
		rsp.setData(createdSpeech);
		return rsp;
	}

	@GetMapping(value = "/o/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateGetUpdateDeleteOneSpeechResponse getSpeechById(@PathVariable String id) {
		Speech existSpeech = speechSvc.getSpeechById(id);
		CreateGetUpdateDeleteOneSpeechResponse rsp = new CreateGetUpdateDeleteOneSpeechResponse();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage("Operation successful");
		rsp.setData(existSpeech);
		return rsp;
	}

	@DeleteMapping(value = "/o/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateGetUpdateDeleteOneSpeechResponse deleteSpeechById(@PathVariable String id) {
		Speech deletedSpeech = speechSvc.deleteSpeechById(id);
		CreateGetUpdateDeleteOneSpeechResponse rsp = new CreateGetUpdateDeleteOneSpeechResponse();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage("Speech was deleted");
		rsp.setData(deletedSpeech);
		return rsp;
	}

	@PatchMapping(value = "/o/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateGetUpdateDeleteOneSpeechResponse partialUpdateSpeech(
		@PathVariable String id,
		@RequestBody Speech speech) {
		Speech updatedSpeech = speechSvc.partialUpdateSpeech(id, speech);
		CreateGetUpdateDeleteOneSpeechResponse rsp = new CreateGetUpdateDeleteOneSpeechResponse();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage("Speech was updated");
		rsp.setData(updatedSpeech);
		return rsp;
	}

	@GetMapping(value = "/_search", produces = MediaType.APPLICATION_JSON_VALUE)
	public SearchSpeechResponse searchSpeechByGet(
		@RequestParam(required = false) String authors,
		@RequestParam(required = false) String dateRangeFrom,
		@RequestParam(required = false) String dateRangeTo,
		@RequestParam(required = false) String keywords,
		@RequestParam(required = false) String snippetsOfTexts) {
		ListResponseContainer<Speech> matchedSpeechExcerpts = speechSvc.searchSpeech(authors, dateRangeFrom, dateRangeTo, keywords, snippetsOfTexts);
		SearchSpeechResponse rsp = new SearchSpeechResponse();
		GenericResponseHeader hdr = new GenericResponseHeader();
		rsp.setHeader(hdr);
		hdr.setStatusCode(String.valueOf(HttpStatus.OK.value()));
		hdr.setStatusMessage("Operation successful");
		rsp.setData(matchedSpeechExcerpts);
		return rsp;
	}
}

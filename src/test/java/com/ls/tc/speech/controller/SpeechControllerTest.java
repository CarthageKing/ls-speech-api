package com.ls.tc.speech.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ls.tc.speech.config.CommonConfig;
import com.ls.tc.speech.config.TestDbConfig;
import com.ls.tc.speech.config.TestSpringConfig;
import com.ls.tc.speech.controller.model.CreateGetUpdateDeleteOneSpeechResponse;
import com.ls.tc.speech.controller.model.ErrorList;
import com.ls.tc.speech.controller.model.ErrorListResponse;
import com.ls.tc.speech.controller.model.GenericResponse;
import com.ls.tc.speech.controller.model.SearchSpeechResponse;
import com.ls.tc.speech.controller.model.Speech;
import com.ls.tc.speech.service.DbHelper;
import com.ls.tc.speech.util.SpeechAppConstants;

import jakarta.annotation.Resource;

@ContextConfiguration(classes = { TestSpringConfig.class, CommonConfig.class, TestDbConfig.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SpeechControllerTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SpeechControllerTest.class);

	private static final ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@LocalServerPort
	private int port;

	@Resource
	private DbHelper dbHelper;

	private String baseUrl;

	// use of a client request factory other than the default, since we're using PATCH method and the UrlConnection
	// provided with Java does not support PATCH
	private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		baseUrl = "http://localhost:" + port;
		dbHelper.truncateAllData();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	void test_CRUD() throws Exception {
		Assertions.assertEquals(0, countSpeechRecords());

		// retrieve a non-existent speech
		try {
			getSpeechById("notexist");
			Assertions.fail("did not throw expected exception");
		} catch (HttpClientErrorException e) {
			LOG.trace("the error response: {}", e.getResponseBodyAsString());
			Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			GenericResponse<String> rspObj = OBJECT_MAPPER.readValue(e.getResponseBodyAsString(), GenericResponse.class);
			Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(true, rspObj.getHeader().getStatusMessage().contains("Cannot find speech"));
		}

		// create some speeches
		Speech sp01 = createSpeech(LocalDate.of(2011, 12, 13),
			// authors
			Arrays.asList("Boston Journal", "Linda Johnson"),
			// keywords
			Arrays.asList("fox", "lazy dog"),
			"The quick brown fox jumps over the lazy dog.");
		Assertions.assertEquals(1, countSpeechRecords());
		Speech spForDelete = createSpeech(LocalDate.of(2007, 5, 25),
			Arrays.asList("Dr. Shrodinger", "Dr. Seuss"),
			Arrays.asList("ASMR", "cat purring"),
			"When a cat is content, your can hear it purr.");
		Assertions.assertEquals(2, countSpeechRecords());

		// get the speech that was first created
		{
			CreateGetUpdateDeleteOneSpeechResponse rspObj = getSpeechById(sp01.getId());
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(sp01.getId(), speech.getId());
			Assertions.assertEquals(sp01.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(sp01.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(sp01.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(sp01.getKeywords(), speech.getKeywords());
		}

		// get the speech to be deleted
		{
			CreateGetUpdateDeleteOneSpeechResponse rspObj = getSpeechById(spForDelete.getId());
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			spForDelete = rspObj.getData();
		}

		// modify a non-existent speech
		{
			Speech updSpeech = new Speech();
			updSpeech.setSpeechDate(LocalDate.of(2018, 9, 3));
			try {
				partialUpdateSpeech(spForDelete.getId() + "5", updSpeech);
				Assertions.fail("did not throw expected exception");
			} catch (HttpClientErrorException e) {
				LOG.trace("the error response: {}", e.getResponseBodyAsString());
				Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
				GenericResponse<String> rspObj = OBJECT_MAPPER.readValue(e.getResponseBodyAsString(), GenericResponse.class);
				Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), rspObj.getHeader().getStatusCode());
				Assertions.assertEquals(true, rspObj.getHeader().getStatusMessage().contains("Cannot find speech"));
			}
		}

		// modify speech date
		{
			Speech updSpeech = new Speech();
			updSpeech.setSpeechDate(LocalDate.of(2018, 9, 3));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertNotEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify speech text
		{
			Speech updSpeech = new Speech();
			updSpeech.setSpeechText("Veni, vici, vidi!");
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertNotEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify authors (complete replace)
		{
			Speech updSpeech = new Speech();
			updSpeech.getAuthors().addAll(Arrays.asList("Julis Ceasar", "Horatio"));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertNotEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify authors (remove one, keep one, add one)
		{
			Speech updSpeech = new Speech();
			updSpeech.getAuthors().addAll(Arrays.asList("Horatio", "Shakespeare"));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertNotEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify keywords (complete replace)
		{
			Speech updSpeech = new Speech();
			updSpeech.getKeywords().addAll(Arrays.asList("Divine Comedy", "Nonsense"));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertNotEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify keywords (remove one, keep one, add one)
		{
			Speech updSpeech = new Speech();
			updSpeech.getKeywords().addAll(Arrays.asList("nonsense", "Nonsense"));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertNotEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		// modify multiple things
		{
			Speech updSpeech = new Speech();
			updSpeech.setSpeechText("the quick black bear");
			updSpeech.getAuthors().addAll(Arrays.asList("nobraon"));
			updSpeech.getKeywords().addAll(Arrays.asList("sameson"));
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(spForDelete.getId(), updSpeech);
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), speech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertNotEquals(spForDelete.getSpeechText(), speech.getSpeechText());
			Assertions.assertNotEquals(spForDelete.getAuthors(), speech.getAuthors());
			Assertions.assertNotEquals(spForDelete.getKeywords(), speech.getKeywords());
			spForDelete = speech;
		}

		Assertions.assertEquals(2, countSpeechRecords());

		// delete the speech
		{
			CreateGetUpdateDeleteOneSpeechResponse rspObj = deleteSpeechById(spForDelete.getId());
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech deletedSpeech = rspObj.getData();
			Assertions.assertEquals(spForDelete.getId(), deletedSpeech.getId());
			Assertions.assertEquals(spForDelete.getSpeechDate(), deletedSpeech.getSpeechDate());
			Assertions.assertEquals(spForDelete.getSpeechText(), deletedSpeech.getSpeechText());
			Assertions.assertEquals(spForDelete.getAuthors(), deletedSpeech.getAuthors());
			Assertions.assertEquals(spForDelete.getKeywords(), deletedSpeech.getKeywords());
		}

		// try to delete the already deleted speech
		try {
			deleteSpeechById(spForDelete.getId());
			Assertions.fail("did not throw expected exception");
		} catch (HttpClientErrorException e) {
			LOG.trace("the error response: {}", e.getResponseBodyAsString());
			Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			GenericResponse<String> rspObj = OBJECT_MAPPER.readValue(e.getResponseBodyAsString(), GenericResponse.class);
			Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(true, rspObj.getHeader().getStatusMessage().contains("Cannot find speech"));
		}

		// attempt to get the deleted speech
		try {
			getSpeechById(spForDelete.getId());
			Assertions.fail("did not throw expected exception");
		} catch (HttpClientErrorException e) {
			LOG.trace("the error response: {}", e.getResponseBodyAsString());
			Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
			GenericResponse<String> rspObj = OBJECT_MAPPER.readValue(e.getResponseBodyAsString(), GenericResponse.class);
			Assertions.assertEquals(String.valueOf(HttpStatus.NOT_FOUND.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(true, rspObj.getHeader().getStatusMessage().contains("Cannot find speech"));
		}

		Assertions.assertEquals(1, countSpeechRecords());
	}

	@Test
	void test_Search() throws Exception {
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search");
			Assertions.assertEquals(0, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(0, rspObj.getData().getEntries().size());
		}

		// create some speeches
		Speech sp01 = createSpeech(LocalDate.of(2011, 12, 13),
			// authors
			Arrays.asList("Arnold Schwarzenegger", "Kevin Sorbo"),
			// keywords
			Arrays.asList("Famous HERCULES stArs", "Live Action", "Muscles"),
			"Hercules is the embodiment of peak physical strength. Also, note to editor, please update this speech.");
		Speech sp02 = createSpeech(LocalDate.of(2015, 3, 24),
			Arrays.asList("Vin Diesel", "Dwayne Johnson", "Paul Walker"),
			Arrays.asList("RACE cars", "TOYOTA SUPRA", "Nitro", "Muscle car"),
			"The FAST AND THE FURIOUS movie series now spans more than 5 movies including spin-offs. The most famous quote is, \"I'll be back.\" No, that's the wrong movie.");
		Speech sp03 = createSpeech(LocalDate.of(2003, 6, 7),
			Arrays.asList("Christopher Walken"),
			Arrays.asList("Hollywood STARDOM", "Unique actor"),
			"Back home, I do the same things every day. Exactly the same. I eat at the same time, I get up at the same time, I do the same things in the same order. I read. I have coffee. Then I study my scripts, I exercise on the treadmill, I make myself a little something to eat. I am a great believer in the Mediterranean diet.");

		// search (no filter criteria provided)
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search");
			Assertions.assertEquals(3, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(3, rspObj.getData().getEntries().size());
			// check the order of the records
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(2).getId());
		}

		// search on a single author
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?authors=SCHWARZENEGGER");
			Assertions.assertEquals(1, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(1, rspObj.getData().getEntries().size());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(0).getId());
		}
		// search on any of the provided authors
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?authors=vin|walk");
			Assertions.assertEquals(3, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(3, rspObj.getData().getEntries().size());
			// check the order of the records
			// this matches sp01 since it contains an author who has the word 'vin' i.e. Kevin Sorbo
			// this matches sp02 since it contains authors who contains either the words 'vin' or 'walk' i.e. Paul Walker and Vin Diesel
			// this matches sp03 since it contains an author who has the word 'walk' i.e. Christopher Walken
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(2).getId());
		}

		// search on a single keyword
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?keywords=AcT");
			Assertions.assertEquals(2, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(2, rspObj.getData().getEntries().size());
			// check the order of the records
			// this matches sp01 since it contains a keyword who has the word 'act' i.e. Live Action
			// this matches sp03 since it contains a keyword who has the word 'act' i.e. Unique actor
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
		}
		// search on any of the provided keywords
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?keywords=AcT|mUscles");
			Assertions.assertEquals(2, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(2, rspObj.getData().getEntries().size());
			// check the order of the records
			// this matches sp01 since it contains a keyword who has the word 'act' i.e. Live Action
			// this matches sp03 since it contains a keyword who has the word 'act' i.e. Unique actor
			// this does not include sp02 since it only has a keyword that has 'muscle' and not 'muscles'
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
		}
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?keywords=AcT|mUscle");
			Assertions.assertEquals(3, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(3, rspObj.getData().getEntries().size());
			// check the order of the records
			// since we updated the search query to only use 'muscle', then sp02 gets included
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(2).getId());
		}

		// search on a single speech snippet
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?snippetsOfTexts=i'll be back");
			Assertions.assertEquals(1, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(1, rspObj.getData().getEntries().size());
			// sp02 is the only record with this snippet
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(0).getId());
		}
		// search on any of the provided speech snippets
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?snippetsOfTexts=i'll be book|bacK");
			Assertions.assertEquals(2, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(2, rspObj.getData().getEntries().size());
			// sp02 and sp03 get included because of 'back'
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(1).getId());
		}

		// get records with speech date matching from
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeFrom=2005-01-01");
			Assertions.assertEquals(2, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(2, rspObj.getData().getEntries().size());
			// only sp01 and sp03 had a speech date beyond beyond 2005
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp02.getId(), rspObj.getData().getEntries().get(1).getId());
		}
		// get records with speech date before given end date
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeTo=2013-01-01");
			Assertions.assertEquals(2, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(2, rspObj.getData().getEntries().size());
			// only sp01 and sp03 had a speech date within the given range
			Assertions.assertEquals(sp03.getId(), rspObj.getData().getEntries().get(0).getId());
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(1).getId());
		}

		// combo search!
		{
			SearchSpeechResponse rspObj = searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeFrom=2005-01-01&dateRangeTo=2013-01-01&authors=Kevin Sorbo|ArNOLD&keywords=muscles|FAMous&snippetsOfTexts=is the embodiment|note to editor, please");
			Assertions.assertEquals(1, rspObj.getData().getTotalRecords());
			Assertions.assertEquals(1, rspObj.getData().getEntries().size());
			// only sp01 matches all the criteria
			Assertions.assertEquals(sp01.getId(), rspObj.getData().getEntries().get(0).getId());
		}
	}

	@Test
	void test_createSpeech_validationErrors() throws Exception {
		// create a completely empty object
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> createSpeech(s -> "{}"));
			Assertions.assertEquals(4, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechDate' must not be null"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechText' must not be blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' must not be empty"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' must not be empty"));
		}
		// it contains the properties but they have explicitly been set to null
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": null,\n"
				+ "  \"speechDate\": null,\n"
				+ "  \"speechText\": null,\n"
				+ "  \"authors\": null,\n"
				+ "  \"keywords\": null\n"
				+ "}"));
			Assertions.assertEquals(4, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechDate' must not be null"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechText' must not be blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' must not be empty"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' must not be empty"));
		}
		// blank strings and empty arrays not allowed
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": null,\n"
				+ "  \"speechDate\": \"2012-04-05\",\n"
				+ "  \"speechText\": \"   \",\n"
				+ "  \"authors\": [],\n"
				+ "  \"keywords\": []\n"
				+ "}"));
			Assertions.assertEquals(3, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechText' must not be blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' must not be empty"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' must not be empty"));
		}
		// speechDate parsing is strict
		try {
			invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": null,\n"
				+ "  \"speechDate\": \"2013-02-29\",\n" // not a leap year
				+ "  \"speechText\": \"   \",\n"
				+ "  \"authors\": [],\n"
				+ "  \"keywords\": []\n"
				+ "}"));
			Assertions.fail("did not throw expected exception");
		} catch (UnrecognizedPropertyException e) {
			// TODO: This is not the behavior we want. Behavior should be consistent across all the fields. Figure this out
			Assertions.assertEquals(true, e.getMessage().contains("Unrecognized field"));
		}
		try {
			invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": null,\n"
				+ "  \"speechDate\": \"2013-01-32\",\n" // invalid and should not spill over (i.e. be converted to 2013-02-01)
				+ "  \"speechText\": \"   \",\n"
				+ "  \"authors\": [],\n"
				+ "  \"keywords\": []\n"
				+ "}"));
			Assertions.fail("did not throw expected exception");
		} catch (UnrecognizedPropertyException e) {
			// TODO: This is not the behavior we want. Behavior should be consistent across all the fields. Figure this out
			Assertions.assertEquals(true, e.getMessage().contains("Unrecognized field"));
		}
		// don't allow explicit nulls as well as blanks inside the arrays
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": null,\n"
				+ "  \"speechDate\": \"2012-04-05\",\n"
				+ "  \"speechText\": \"jacobson blah\",\n"
				+ "  \"authors\": [\"authorb\", null, \"authora\", \"\"],\n"
				+ "  \"keywords\": [\"   \", \"   stone\", null]\n"
				+ "}"));
			Assertions.assertEquals(4, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 1 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 3 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 0 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 2 must not be null or completely blank"));
		}
		// checking of string length limits
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> createSpeech(s -> "{\n"
				+ "  \"id\": \"" + generateStringOfLength(SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH + 1) + "\",\n"
				+ "  \"speechDate\": \"2005-08-09\",\n"
				+ "  \"speechText\": \"" + generateStringOfLength(SpeechAppConstants.SPEECH_TEXT_MAX_LENGTH + 1) + "\",\n"
				+ "  \"authors\": [\n"
				+ "    \"" + generateStringOfLength(SpeechAppConstants.SPEECH_AUTHOR_MAX_LENGTH + 1) + "\"\n"
				+ "  ],\n"
				+ "  \"keywords\": [\n"
				+ "    \"" + generateStringOfLength(SpeechAppConstants.SPEECH_KEYWORD_MAX_LENGTH + 1) + "\"\n"
				+ "  ]\n"
				+ "}"));
			Assertions.assertEquals(4, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 0 must have maximum length of 1024"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechText' size must be between 0 and 1048576"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'id' size must be between 0 and 64"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 0 must have maximum length of 1024"));
		}
	}

	@Test
	void test_partialUpdateSpeech_validationErrors() throws Exception {
		Speech sp01 = createSpeech(LocalDate.of(2011, 12, 13),
			// authors
			Arrays.asList("Boston Journal", "Linda Johnson"),
			// keywords
			Arrays.asList("fox", "lazy dog"),
			"The quick brown fox jumps over the lazy dog.");

		// below won't update anything if nothing is indicated for update
		{
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(sp01.getId(), "{}");
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(sp01.getId(), speech.getId());
			Assertions.assertEquals(sp01.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(sp01.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(sp01.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(sp01.getKeywords(), speech.getKeywords());
			sp01 = speech;
		}
		// id cannot be updated
		{
			CreateGetUpdateDeleteOneSpeechResponse rspObj = partialUpdateSpeech(sp01.getId(), "{\n"
				+ "  \"id\": \"sfd34\""
				+ "}");
			Assertions.assertEquals(String.valueOf(HttpStatus.OK.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
			Speech speech = rspObj.getData();
			Assertions.assertEquals(sp01.getId(), speech.getId());
			Assertions.assertEquals(sp01.getSpeechDate(), speech.getSpeechDate());
			Assertions.assertEquals(sp01.getSpeechText(), speech.getSpeechText());
			Assertions.assertEquals(sp01.getAuthors(), speech.getAuthors());
			Assertions.assertEquals(sp01.getKeywords(), speech.getKeywords());
			sp01 = speech;
		}
		// id is ignored for updates, but still checked for validation
		{
			Speech[] fixer = { sp01 };
			ErrorList errlst = invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "  \"id\": \"" + generateStringOfLength(SpeechAppConstants.SPEECH_ENTITY_ID_MAX_LENGTH + 1) + "\""
				+ "}"));
			Assertions.assertEquals(1, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'id' size must be between 0 and 64"));
		}
		// speechDate parsing is strict
		try {
			Speech[] fixer = { sp01 };
			invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "  \"speechDate\": \"2013-02-29\"" // not a leap year
				+ "}"));
			Assertions.fail("did not throw expected exception");
		} catch (UnrecognizedPropertyException e) {
			// TODO: This is not the behavior we want. Behavior should be consistent across all the fields. Figure this out
			Assertions.assertEquals(true, e.getMessage().contains("Unrecognized field"));
		}
		try {
			Speech[] fixer = { sp01 };
			invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "  \"speechDate\": \"2013-01-32\"" // invalid and should not spill over (i.e. be converted to 2013-02-01)
				+ "}"));
			Assertions.fail("did not throw expected exception");
		} catch (UnrecognizedPropertyException e) {
			// TODO: This is not the behavior we want. Behavior should be consistent across all the fields. Figure this out
			Assertions.assertEquals(true, e.getMessage().contains("Unrecognized field"));
		}
		// checking speechText length limits
		{
			Speech[] fixer = { sp01 };
			ErrorList errlst = invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "    \"speechText\": \"" + generateStringOfLength(SpeechAppConstants.SPEECH_TEXT_MAX_LENGTH + 1) + "\""
				+ "}"));
			Assertions.assertEquals(1, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'speechText' size must be between 0 and 1048576"));
		}
		// authors checks
		{
			Speech[] fixer = { sp01 };
			ErrorList errlst = invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "  \"authors\": [\"authorb\", null, \"authora\", \"\",\"" + generateStringOfLength(SpeechAppConstants.SPEECH_AUTHOR_MAX_LENGTH + 1) + "\"]\n"
				+ "}"));
			Assertions.assertEquals(3, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 1 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 3 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'authors' entry at index 4 must have maximum length of 1024"));
		}
		// keywords checks
		{
			Speech[] fixer = { sp01 };
			ErrorList errlst = invokeExpectingHttpClientError(() -> partialUpdateSpeech(fixer[0].getId(), "{\n"
				+ "  \"keywords\": [\"   \", \"   stone\", null,\"" + generateStringOfLength(SpeechAppConstants.SPEECH_KEYWORD_MAX_LENGTH + 1) + "\"]\n"
				+ "}"));
			Assertions.assertEquals(3, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 0 must not be null or completely blank"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 3 must have maximum length of 1024"));
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'keywords' entry at index 2 must not be null or completely blank"));
		}
	}

	@Test
	void test_searchSpeechByGet_validationErrors() throws Exception {
		// dates are strictly parsed
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeFrom=2013-02-29"));
			Assertions.assertEquals(1, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'dateRangeFrom' not a valid date"));
		}
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeTo=2005-09-31"));
			Assertions.assertEquals(1, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'dateRangeTo' not a valid date"));
		}
		// from should be equal or lesser than to
		{
			ErrorList errlst = invokeExpectingHttpClientError(() -> searchSpeechByGet(baseUrl + "/speeches/_search?dateRangeFrom=2005-09-30&dateRangeTo=2005-09-13"));
			Assertions.assertEquals(1, errlst.getErrorMessages().size());
			Assertions.assertEquals(true, containsErrorMessage(errlst.getErrorMessages(), "'dateRangeFrom' must be equal or less than 'dateRangeTo'"));
		}
	}

	private String generateStringOfLength(int length) {
		StringBuilder sb = new StringBuilder();
		String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(r.nextInt(chars.length())));
		}
		return sb.toString();
	}

	private boolean containsErrorMessage(List<String> errorMessages, String str) {
		for (String err : errorMessages) {
			if (err.contains(str)) {
				return true;
			}
		}
		return false;
	}

	private ErrorList invokeExpectingHttpClientError(MyFunc func) throws Exception {
		try {
			func.doIt();
			Assertions.fail("did not throw expected exception");
			return null;
		} catch (HttpClientErrorException e) {
			LOG.trace("the error response: {}", e.getResponseBodyAsString());
			Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
			ErrorListResponse rspObj = OBJECT_MAPPER.readValue(e.getResponseBodyAsString(), ErrorListResponse.class);
			Assertions.assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), rspObj.getHeader().getStatusCode());
			Assertions.assertEquals(true, rspObj.getHeader().getStatusMessage().contains("Validation failure"));
			return rspObj.getData();
		}
	}

	private SearchSpeechResponse searchSpeechByGet(String requestUriStr) throws JsonProcessingException, JsonMappingException {
		String content = null;
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.GET, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		SearchSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), SearchSpeechResponse.class);
		return rspObj;
	}

	private int countSpeechRecords() throws Exception {
		String requestUriStr = baseUrl + "/speeches/_search";
		String content = null;
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.GET, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		SearchSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), SearchSpeechResponse.class);
		return rspObj.getData().getTotalRecords();
	}

	private CreateGetUpdateDeleteOneSpeechResponse deleteSpeechById(String speechId) throws Exception {
		String requestUriStr = baseUrl + "/speeches/o/" + speechId;
		String content = null;
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.DELETE, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		CreateGetUpdateDeleteOneSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), CreateGetUpdateDeleteOneSpeechResponse.class);
		return rspObj;
	}

	private CreateGetUpdateDeleteOneSpeechResponse getSpeechById(String speechId) throws Exception {
		String requestUriStr = baseUrl + "/speeches/o/" + speechId;
		String content = null;
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.GET, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		CreateGetUpdateDeleteOneSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), CreateGetUpdateDeleteOneSpeechResponse.class);
		return rspObj;
	}

	private Speech createSpeech(LocalDate speechDate, List<String> authors, List<String> keywords, String speechText) throws Exception {
		Speech retSpeech = createSpeech(s -> {
			Speech speech = new Speech();
			speech.setSpeechDate(speechDate);
			speech.setSpeechText(speechText);
			speech.getAuthors().addAll(authors);
			speech.getKeywords().addAll(keywords);
			try {
				return OBJECT_MAPPER.writeValueAsString(speech);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
		Assertions.assertNotNull(retSpeech.getId());
		Assertions.assertEquals(speechDate, retSpeech.getSpeechDate());
		Assertions.assertEquals(speechText, retSpeech.getSpeechText());
		Assertions.assertEquals(authors, retSpeech.getAuthors());
		Assertions.assertEquals(keywords, retSpeech.getKeywords());
		return retSpeech;
	}

	private Speech createSpeech(Function<Void, String> contentCreator) throws Exception {
		String requestUriStr = baseUrl + "/speeches/o";
		String content = contentCreator.apply(null);
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.POST, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.CREATED, httpRsp.getStatusCode());
		CreateGetUpdateDeleteOneSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), CreateGetUpdateDeleteOneSpeechResponse.class);
		Assertions.assertEquals(String.valueOf(HttpStatus.CREATED.value()), rspObj.getHeader().getStatusCode());
		Assertions.assertEquals(false, rspObj.getHeader().getStatusMessage().isEmpty());
		Speech speech = rspObj.getData();

		return speech;
	}

	private CreateGetUpdateDeleteOneSpeechResponse partialUpdateSpeech(String speechId, Speech updatedSpeech) throws Exception {
		return partialUpdateSpeech(speechId, OBJECT_MAPPER.writeValueAsString(updatedSpeech));
	}

	private CreateGetUpdateDeleteOneSpeechResponse partialUpdateSpeech(String speechId, String speechJsonContent) throws Exception {
		String requestUriStr = baseUrl + "/speeches/o/" + speechId;
		String content = speechJsonContent;
		LOG.trace("the request: {}", content);
		HttpHeaders hdrs = new HttpHeaders();
		hdrs.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		hdrs.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<String> httpReq = new HttpEntity<String>(content, hdrs);
		ResponseEntity<String> httpRsp = restTemplate.exchange(requestUriStr, HttpMethod.PATCH, httpReq, String.class);
		LOG.trace("the response: {}", httpRsp.getBody());
		Assertions.assertEquals(HttpStatus.OK, httpRsp.getStatusCode());
		CreateGetUpdateDeleteOneSpeechResponse rspObj = OBJECT_MAPPER.readValue(httpRsp.getBody(), CreateGetUpdateDeleteOneSpeechResponse.class);
		return rspObj;
	}

	@FunctionalInterface
	public interface MyFunc {
		void doIt() throws Exception;
	}
}

package com.ls.tc.speech.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ls.tc.speech.controller.model.ListResponseContainer;
import com.ls.tc.speech.controller.model.Speech;
import com.ls.tc.speech.dao.SpeechAuthorityEntityDao;
import com.ls.tc.speech.dao.SpeechEntityDao;
import com.ls.tc.speech.dao.SpeechKeywordEntityDao;
import com.ls.tc.speech.dao.SpeechSearchEntityDao;
import com.ls.tc.speech.dao.entity.SpeechAuthorEntity;
import com.ls.tc.speech.dao.entity.SpeechAuthorEntity.SpeechAuthorityEntityPK;
import com.ls.tc.speech.dao.entity.SpeechEntity;
import com.ls.tc.speech.dao.entity.SpeechKeywordEntity;
import com.ls.tc.speech.dao.entity.SpeechKeywordEntity.SpeechKeywordEntityPK;
import com.ls.tc.speech.exception.SpeechAppRecordNotFoundException;
import com.ls.tc.speech.exception.SpeechAppValidationFailedException;
import com.ls.tc.speech.util.SpeechSpeechEntityMapper;
import com.ls.tc.speech.validation.CreateSpeechValidationGroup;
import com.ls.tc.speech.validation.PartialUpdateSpeechValidationGroup;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
@Transactional
public class SpeechService {

	@Resource
	private SpeechEntityDao speechDao;

	@Resource
	private SpeechSearchEntityDao speechSearchDao;

	@Resource
	private SpeechAuthorityEntityDao speechAuthorDao;

	@Resource
	private SpeechKeywordEntityDao speechKeywordDao;

	@Resource
	private Validator validator;

	public SpeechService() {
		// noop
	}

	public Speech createSpeech(Speech speech) {
		Set<ConstraintViolation<Speech>> errors = validator.validate(speech, CreateSpeechValidationGroup.class);
		if (!CollectionUtils.isEmpty(errors)) {
			throw new SpeechAppValidationFailedException(errors);
		}

		SpeechEntity spEnt = new SpeechEntity();
		mapToSpeechEntity(speech, spEnt);
		spEnt = speechDao.save(spEnt);

		List<SpeechAuthorEntity> spaEntLst = new ArrayList<>();
		for (String author : speech.getAuthors()) {
			SpeechAuthorEntity sae = createSpeechAuthorEntityFrom(spEnt.getId(), author);
			sae = speechAuthorDao.save(sae);
			spaEntLst.add(sae);
		}

		List<SpeechKeywordEntity> spkEntLst = new ArrayList<>();
		for (String keyword : speech.getKeywords()) {
			SpeechKeywordEntity ske = createSpeechKeywordEntityFrom(spEnt.getId(), keyword);
			ske = speechKeywordDao.save(ske);
			spkEntLst.add(ske);
		}

		speech.setId(spEnt.getId());
		return speech;
	}

	private void mapToSpeechEntity(Speech speech, SpeechEntity spEnt) {
		SpeechEntity e = SpeechSpeechEntityMapper.INSTANCE.speechToSpeechEntity(speech);
		spEnt.setSpeechDate(e.getSpeechDate());
		spEnt.setSpeechText(e.getSpeechText());
	}

	private void mapToSpeech(SpeechEntity spEnt, Speech speech) {
		Speech s = SpeechSpeechEntityMapper.INSTANCE.speechEntityToSpeech(spEnt);
		speech.setId(s.getId());
		speech.setSpeechDate(s.getSpeechDate());
		speech.setSpeechText(s.getSpeechText());
	}

	private SpeechAuthorEntity createSpeechAuthorEntityFrom(String id, String author) {
		SpeechAuthorEntity sae = new SpeechAuthorEntity();
		sae.setId(new SpeechAuthorityEntityPK(id, author));
		return sae;
	}

	private SpeechKeywordEntity createSpeechKeywordEntityFrom(String id, String keyword) {
		SpeechKeywordEntity ske = new SpeechKeywordEntity();
		ske.setId(new SpeechKeywordEntityPK(id, keyword));
		return ske;
	}

	public Speech getSpeechById(String speechId) {
		Optional<SpeechEntity> spEntOpt = speechDao.findById(speechId);
		if (spEntOpt.isEmpty()) {
			throw new SpeechAppRecordNotFoundException("Cannot find speech with that id");
		}
		Speech retSpeech = new Speech();
		mapToSpeech(spEntOpt.get(), retSpeech);

		for (SpeechAuthorEntity sae : speechAuthorDao.findByIdSpeechId(retSpeech.getId())) {
			retSpeech.getAuthors().add(sae.getId().getAuthor());
		}

		for (SpeechKeywordEntity ske : speechKeywordDao.findByIdSpeechId(retSpeech.getId())) {
			retSpeech.getKeywords().add(ske.getId().getKeyword());
		}

		return retSpeech;
	}

	public Speech deleteSpeechById(String speechId) {
		Optional<SpeechEntity> spEntOpt = speechDao.findById(speechId);
		if (spEntOpt.isEmpty()) {
			throw new SpeechAppRecordNotFoundException("Cannot find speech with that id");
		}
		Speech retSpeech = new Speech();
		mapToSpeech(spEntOpt.get(), retSpeech);

		List<SpeechAuthorEntity> saelst = speechAuthorDao.findByIdSpeechId(retSpeech.getId());
		saelst.forEach(sae -> retSpeech.getAuthors().add(sae.getId().getAuthor()));
		speechAuthorDao.deleteAll(saelst);

		List<SpeechKeywordEntity> skelst = speechKeywordDao.findByIdSpeechId(retSpeech.getId());
		skelst.forEach(ske -> retSpeech.getKeywords().add(ske.getId().getKeyword()));
		speechKeywordDao.deleteAll(skelst);

		speechDao.delete(spEntOpt.get());

		return retSpeech;
	}

	public Speech partialUpdateSpeech(String id, Speech speech) {
		Set<ConstraintViolation<Speech>> errors = validator.validate(speech, PartialUpdateSpeechValidationGroup.class);
		if (!CollectionUtils.isEmpty(errors)) {
			throw new SpeechAppValidationFailedException(errors);
		}

		Optional<SpeechEntity> spEntOpt = speechDao.findById(id);
		if (spEntOpt.isEmpty()) {
			throw new SpeechAppRecordNotFoundException("Cannot find speech with that id");
		}
		SpeechEntity existSpEnt = spEntOpt.get();

		if (null != speech.getSpeechDate()) {
			existSpEnt.setSpeechDate(Date.valueOf(speech.getSpeechDate().toString()));
		}
		if (null != speech.getSpeechText()) {
			existSpEnt.setSpeechText(speech.getSpeechText());
		}

		if (!CollectionUtils.isEmpty(speech.getAuthors())) {
			List<SpeechAuthorEntity> lst = speechAuthorDao.findByIdSpeechId(id);
			// add new from incoming list
			for (String author : speech.getAuthors()) {
				if (null == findSpeechAuthorEntityWithAuthor(lst, author)) {
					SpeechAuthorEntity sae = createSpeechAuthorEntityFrom(existSpEnt.getId(), author);
					sae = speechAuthorDao.save(sae);
				}
			}
			// remove entries not from incoming list
			for (SpeechAuthorEntity sae : lst) {
				if (!speech.getAuthors().contains(sae.getId().getAuthor())) {
					speechAuthorDao.delete(sae);
				}
			}
		}

		if (!CollectionUtils.isEmpty(speech.getKeywords())) {
			List<SpeechKeywordEntity> lst = speechKeywordDao.findByIdSpeechId(id);
			// add new from incoming list
			for (String keyword : speech.getKeywords()) {
				if (null == findSpeechKeywordEntityWithKeyword(lst, keyword)) {
					SpeechKeywordEntity ske = createSpeechKeywordEntityFrom(existSpEnt.getId(), keyword);
					ske = speechKeywordDao.save(ske);
				}
			}
			// remove entries not from the incoming list
			for (SpeechKeywordEntity ske : lst) {
				if (!speech.getKeywords().contains(ske.getId().getKeyword())) {
					speechKeywordDao.delete(ske);
				}
			}
		}

		return getSpeechById(id);
	}

	private SpeechKeywordEntity findSpeechKeywordEntityWithKeyword(List<SpeechKeywordEntity> lst, String keyword) {
		for (SpeechKeywordEntity ske : lst) {
			if (ske.getId().getKeyword().equals(keyword)) {
				return ske;
			}
		}
		return null;
	}

	private SpeechAuthorEntity findSpeechAuthorEntityWithAuthor(List<SpeechAuthorEntity> lst, String author) {
		for (SpeechAuthorEntity sae : lst) {
			if (sae.getId().getAuthor().equals(author)) {
				return sae;
			}
		}
		return null;
	}

	public ListResponseContainer<Speech> searchSpeech(String authors, String dateRangeFrom, String dateRangeTo, String keywords, String snippetsOfTexts) {
		List<String> idsLst = speechSearchDao.getIdsMatchingCriteria(authors, dateRangeFrom, dateRangeTo, keywords, snippetsOfTexts);
		ListResponseContainer<Speech> result = new ListResponseContainer<>();
		for (String speechId : idsLst) {
			result.getEntries().add(getSpeechById(speechId));
		}
		result.setTotalRecords(result.getEntries().size());
		return result;
	}
}

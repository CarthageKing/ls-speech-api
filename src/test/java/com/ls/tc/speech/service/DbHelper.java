package com.ls.tc.speech.service;

import com.ls.tc.speech.dao.SpeechAuthorityEntityDao;
import com.ls.tc.speech.dao.SpeechEntityDao;
import com.ls.tc.speech.dao.SpeechKeywordEntityDao;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

public class DbHelper {

	@Resource
	private SpeechEntityDao speechDao;

	@Resource
	private SpeechAuthorityEntityDao speechAuthorDao;

	@Resource
	private SpeechKeywordEntityDao speechKeywordDao;

	public DbHelper() {
		// noop
	}

	@Transactional
	public void truncateAllData() {
		speechKeywordDao.deleteAll(speechKeywordDao.findAll());
		speechAuthorDao.deleteAll(speechAuthorDao.findAll());
		speechDao.deleteAll(speechDao.findAll());
	}
}

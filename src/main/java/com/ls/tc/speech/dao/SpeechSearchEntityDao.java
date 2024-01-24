package com.ls.tc.speech.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ls.tc.speech.dao.entity.SpeechEntity;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class SpeechSearchEntityDao {

	@Resource
	private EntityManager entityMgr;

	public SpeechSearchEntityDao() {
		// noop
	}

	@SuppressWarnings("unchecked")
	public List<String> getIdsMatchingCriteria() {
		return entityMgr.createQuery("select a.id from " + SpeechEntity.class.getSimpleName() + " a").getResultList();
	}
}
